package com.enonic.gradle.xp.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

import aQute.bnd.gradle.BundleTaskExtension;

final class BundleConfigurator
{
    private final static String IMPORT_PACKAGE = "Import-Package";

    private final static String PRIVATE_PACKAGE = "Private-Package";

    private final static String DEFAULT_IMPORT = "*;resolution:=optional";

    private static final String SYSTEM_BUNDLE_TYPE = "system";

    private static final String APPLICATION_BUNDLE_TYPE = "application";

    private final Project project;

    private final BundleTaskExtension ext;

    BundleConfigurator( final Project project )
    {
        this.project = project;
        this.ext = (BundleTaskExtension) project.getTasks().getByName( "jar" ).getExtensions().getByName( "bundle" );
    }

    boolean configure( final AppExtension application )
    {
        final String version = Objects.requireNonNullElse( application.getSystemVersion(), "" ).trim();
        if ( version.isEmpty() )
        {
            throw new IllegalArgumentException(
                "XP system version not specified. Please add the following line in the 'app' closure in build.gradle:\r\n  systemVersion = \"${xpVersion}\"" );
        }
        final XpVersion xpVersion = XpVersion.parse( version );
        if ( !xpVersion.valid )
        {
            throw new IllegalArgumentException( "Invalid XP system version: systemVersion = '" + version + "'" );
        }

        validateApplicationName( application.getName() );

        final Configuration libConfig = this.project.getConfigurations().getByName( "include" );
        final Configuration filteredConfig = DependenciesConfigurator.configure( libConfig, xpVersion );
        this.ext.setClasspath( filteredConfig );

        final Map<String, String> instructions = new HashMap<>( application.getInstructions() );

        final String importPackage = instructions.remove( IMPORT_PACKAGE );
        instruction( IMPORT_PACKAGE, importPackage != null ? importPackage : DEFAULT_IMPORT );
        instruction( PRIVATE_PACKAGE, "*;-split-package:=merge-first" );

        instruction( "-removeheaders", "Require-Capability,Include-Resource" );
        instruction( "-nouses", "true" );
        instruction( "-dsannotations", "*" );

        instruction( "Bundle-SymbolicName", application.getName() );
        instruction( "Bundle-Name", application.getDisplayName() );
        instruction( "X-Application-Url", application.getUrl() );
        instruction( "X-Vendor-Name", application.getVendorName() );
        instruction( "X-Vendor-Url", application.getVendorUrl() );
        instruction( "X-System-Version",
                     String.format( "[%s.%s,%s)", xpVersion.major, xpVersion.minor, Math.addExact( xpVersion.major, 1 ) ) );
        instruction( "X-Bundle-Type", application.isSystemApp() ? SYSTEM_BUNDLE_TYPE : APPLICATION_BUNDLE_TYPE );
        instruction( "X-Capability", String.join( ",", application.getCapabilities() ) );

        for ( final Map.Entry<String, String> entry : instructions.entrySet() )
        {
            instruction( entry.getKey(), entry.getValue() );
        }

        includeServiceLoader( filteredConfig );
        includeWebJars();

        return addDevSourcePaths( application.getDevSourcePaths(), application.getRawDevSourcePaths() );
    }

    private void instruction( final String name, final Object value )
    {
        if ( value != null )
        {
            this.ext.bnd( Map.of( name, value.toString() ) );
        }
    }

    private void validateApplicationName( final String name )
    {
        // Initially validated only to not contain `-` for historical reasons.
        // In practice application names (not display names) is more limited:
        // Cannot be `_`, so it cannot conflict with XP reserved endpoint.
        // Must be valid OSGi Bundle-SymbolicName (pre-validate here for clarity, and exclude `-` as well).
        if ( name.equals( "_" ) || !name.matches( "[a-zA-Z0-9_]+(?:\\.[a-zA-Z0-9_]+)*" ) )
        {
            throw new IllegalArgumentException( "Invalid application name [" + name + "]. Name should not contain [-], " +
                                                    "should not be single underscore and should be a valid OSGi Bundle-SymbolicName." );
        }
    }

    private void includeWebJars()
    {
        final Configuration webjarConfiguration = this.project.getConfigurations().getByName( "webjar" );
        this.ext.classpath( webjarConfiguration );
        webjarConfiguration.getFiles()
            .forEach( file -> instruction( "-includeresource.webjar." + file.getName(),
                                           "assets=" + "@" + file.getName() + "!/META-INF/resources/webjars/(*);rename:=$1" ) );
    }

    private void includeServiceLoader( final Configuration filteredConfig )
    {
        final Path tempDirectory = createTempDirectory();

        String serviceloaderResources = filteredConfig.getFiles()
            .stream()
            .map( this::createJarFile )
            .flatMap( jarFile -> jarFile.stream()
                .filter( jarEntry -> !jarEntry.isDirectory() )
                .filter( jarEntry -> jarEntry.getName().startsWith( "META-INF/services/" ) )
                .map( jarEntry -> createServiceFile( jarEntry, jarFile, tempDirectory ) ) )
            .distinct()
            .map( path -> "META-INF/services/" + path.toFile().getName() + "=" + path )
            .collect( Collectors.joining( "," ) );

        if ( !serviceloaderResources.isBlank() )
        {
            instruction( "-includeresource.serviceloader", serviceloaderResources );
        }
    }

    private Path createServiceFile( final JarEntry jarEntry, final JarFile jarFile, final Path directory )
    {
        try
        {
            final Path filePath = Path.of( directory.toString(), jarEntry.getName().substring( jarEntry.getName().lastIndexOf( "/" ) + 1 ) );

            if ( !Files.exists( filePath ) )
            {
                Files.createFile( filePath );
            }

            try (InputStream inputStream = jarFile.getInputStream( jarEntry );

                 final FileOutputStream outputStream = new FileOutputStream( filePath.toFile(), true ))
            {
                copyStream( inputStream, outputStream );
            }

            return filePath;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private static Path createTempDirectory()
    {
        try
        {
            return Files.createTempDirectory( "services" );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    private JarFile createJarFile( final File file )
    {
        try
        {
            return new JarFile( file );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    private void copyStream( InputStream input, FileOutputStream output )
        throws IOException
    {
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ( ( bytesRead = input.read( buffer ) ) != -1 )
        {
            output.write( buffer, 0, bytesRead );
        }
    }

    private boolean addDevSourcePaths( final List<File> paths, final List<String> rawPaths )
    {
        final Object property = project.findProperty( "com.enonic.xp.app.production" );
        if ( "true".equals( property ) || Boolean.TRUE.equals( property ) )
        {
            return false;
        }
        final Set<String> sourcePaths = new LinkedHashSet<>();
        paths.stream()
            .map( File::getAbsolutePath )
            .map( absolutePath -> absolutePath.replace( File.separatorChar, '/' ) )
            .forEach( sourcePaths::add );
        sourcePaths.addAll( rawPaths );
        final String xSourcePaths = String.join( ",", sourcePaths );
        if ( xSourcePaths.isBlank() )
        {
            return false;
        }
        instruction( "X-Source-Paths", xSourcePaths );
        return true;
    }
}
