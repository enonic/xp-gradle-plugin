package com.enonic.gradle.xp.app;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

import aQute.bnd.gradle.BundleTaskConvention;

final class BundleConfigurator
{
    private final static String IMPORT_PACKAGE = "Import-Package";

    private final static String PRIVATE_PACKAGE = "Private-Package";

    private final static String DEFAULT_IMPORT = "*;resolution:=optional";

    private static final String SYSTEM_BUNDLE_TYPE = "system";

    private static final String APPLICATION_BUNDLE_TYPE = "application";

    private final Project project;

    private final BundleTaskConvention ext;

    BundleConfigurator( final Project project, final BundleTaskConvention ext )
    {
        this.project = project;
        this.ext = ext;
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

        includeWebJars();
        includeServiceLoader( filteredConfig );

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
        if ( this.project.getConfigurations().getByName( "webjar" ).isEmpty() )
        {
            return;
        }

        final File webjarsDir = new File( this.project.getBuildDir(), "webjars/META-INF/resources/webjars" );
        instruction( "-includeresource.webjar", "assets=" + webjarsDir.getAbsolutePath().replace( File.separatorChar, '/' ) );
    }

    private void includeServiceLoader( final Configuration filteredConfig )
    {
        String serviceloaderResources = filteredConfig.
            getFiles().
            stream().
            map( file -> "@" + file.getName() + "!/META-INF/services/*" ).
            collect( Collectors.joining( "," ) );
        if ( !serviceloaderResources.isBlank() )
        {
            instruction( "-includeresource.serviceloader", serviceloaderResources );
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
        paths.stream().
            map( File::getAbsolutePath ).
            map( absolutePath -> absolutePath.replace( File.separatorChar, '/' ) ).
            forEach( sourcePaths::add );
        sourcePaths.addAll( rawPaths );
        final String xSourcePaths = String.join( ",", sourcePaths );
        if ( xSourcePaths.isBlank() )
        {
            return false;
        }
        instruction( "X-Source-Paths", sourcePaths );
        return true;
    }
}
