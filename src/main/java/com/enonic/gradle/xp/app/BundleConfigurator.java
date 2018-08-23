package com.enonic.gradle.xp.app;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;

import aQute.bnd.gradle.BundleTaskConvention;

final class BundleConfigurator
{
    private final static String IMPORT_PACKAGE = "Import-Package";

    private final static String PRIVATE_PACKAGE = "Private-Package";

    private final static String DEFAULT_IMPORT = "*;resolution:=optional";

    private static final String SYSTEM_BUNDLE_TYPE = "system";

    private static final String APPLICATION_BUNDLE_TYPE = "application";

    private static final String VERSION_REGEX = "(\\d+\\.\\d+\\.\\d+)(?:-[^-]+)?";

    private final Project project;

    private final BundleTaskConvention ext;

    BundleConfigurator( final Project project, final BundleTaskConvention ext )
    {
        this.project = project;
        this.ext = ext;
    }

    void configure( final AppExtension application )
    {
        final Configuration libConfig = this.project.getConfigurations().getByName( "include" );
        final Configuration filteredConfig = ExcludeRuleConfigurator.configure( libConfig );
        this.ext.setClasspath( filteredConfig );

        final Map<String, String> instructions = new HashMap<>();
        instructions.putAll( application.getInstructions() );

        final String importPackage = instructions.remove( IMPORT_PACKAGE );
        instruction( IMPORT_PACKAGE, importPackage != null ? importPackage : DEFAULT_IMPORT );
        instruction( PRIVATE_PACKAGE, "*;-split-package:=merge-first" );

        instruction( "-removeheaders", "Require-Capability,Include-Resource" );
        instruction( "-nouses", "true" );
        instruction( "-dsannotations", "*" );

        validateXpVersion( application );
        validateApplicationName( application.getName() );

        instruction( "Bundle-SymbolicName", application.getName() );
        instruction( "Bundle-Name", application.getDisplayName() );
        instruction( "X-Application-Url", application.getUrl() );
        instruction( "X-Vendor-Name", application.getVendorName() );
        instruction( "X-Vendor-Url", application.getVendorUrl() );
        instruction( "X-System-Version", application.getSystemVersion() );
        instruction( "X-Bundle-Type", application.isSystemApp() ? SYSTEM_BUNDLE_TYPE : APPLICATION_BUNDLE_TYPE );
        instruction( "X-Capability", Joiner.on( ',' ).skipNulls().join( application.getCapabilities() ) );

        for ( final Map.Entry<String, String> entry : instructions.entrySet() )
        {
            instruction( entry.getKey(), entry.getValue() );
        }

        includeWebJars();
        addDevSourcePaths( application.getDevSourcePaths(), application.getRawDevSourcePaths() );
    }

    private void instruction( final String name, final Object value )
    {
        if ( value != null )
        {
            final Map<String, String> map = Maps.newHashMap();
            map.put( name, value.toString() );
            this.ext.bnd( map );
        }
    }

    private void validateApplicationName( final String name )
    {
        if ( name.contains( "-" ) )
        {
            throw new IllegalArgumentException( "Invalid application name [" + name + "]. Name should not contain [-]." );
        }
    }

    private void validateXpVersion( final AppExtension application )
    {
        String version = application.getSystemVersion();
        if ( version == null || version.trim().isEmpty() )
        {
            throw new IllegalArgumentException(
                "XP system version not specified. Please add the following line in the 'app' closure in build.gradle:\r\n  systemVersion = \"${xpVersion}\"" );
        }
        final Matcher matcher = Pattern.compile( VERSION_REGEX ).matcher( version.trim() );
        if ( !matcher.find() )
        {
            throw new IllegalArgumentException( "Invalid XP system version: systemVersion = '" + version + "'." );
        }

        application.setSystemVersion( getVersionRange( matcher.group( 1 ) ) );
    }

    private String getVersionRange( final String version )
    {
        final long[] versionNumbers = Pattern.compile( "\\." ).splitAsStream( version ).mapToLong( Long::valueOf ).toArray();
        final long upperVersion = versionNumbers[0] + 1;
        return String.format( "[%d.%d,%d)", versionNumbers[0], versionNumbers[1], upperVersion );
    }

    private void includeWebJars()
    {
        if ( this.project.getConfigurations().getByName( "webjar" ).isEmpty() )
        {
            return;
        }

        final File webjarsDir = new File( this.project.getBuildDir(), "webjars/META-INF/resources/webjars" );
        instruction( "Include-Resource", "/assets=" + webjarsDir.getAbsolutePath().replace( File.separatorChar, '/' ) );
    }

    private void addDevSourcePaths( final List<File> paths, final List<String> rawPaths )
    {
        final Iterator<String> it =
            paths.stream().map( File::getAbsolutePath ).map( absolutePath -> absolutePath.replace( File.separatorChar, '/' ) ).iterator();
        final List<String> li = Lists.newArrayList( it );
        li.addAll( rawPaths );
        instruction( "X-Source-Paths", Joiner.on( ',' ).join( li ) );
    }
}
