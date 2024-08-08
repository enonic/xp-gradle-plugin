package com.enonic.gradle.xp.app;

import java.util.HashMap;
import java.util.Map;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ModuleVersionSelector;

final class DependenciesConfigurator
{
    private final Configuration config;

    private final XpVersion systemVersion;

    private DependenciesConfigurator( final Configuration config, final XpVersion systemVersion )
    {
        this.config = config;
        this.systemVersion = systemVersion;
    }

    public static Configuration configure( final Configuration source, final XpVersion systemVersion )
    {
        final Configuration result = source.copy();
        new DependenciesConfigurator( result, systemVersion ).apply();
        return result;
    }

    private void apply()
    {
        config.resolutionStrategy( r -> r.eachDependency( details -> {
            final ModuleVersionSelector requested = details.getRequested();
            if ( requested.getGroup().equals( "com.enonic.xp" ) )
            {
                if ( requested.getName().startsWith( "lib-" ) )
                {
                    final String requestedVersion = requested.getVersion();
                    if ( requestedVersion != null )
                    {
                        final XpVersion requestedXpVersion = XpVersion.parse( requestedVersion );
                        if ( requestedXpVersion.major != systemVersion.major || requestedXpVersion.minor > systemVersion.minor ||
                            ( requestedXpVersion.minor == systemVersion.minor && requestedXpVersion.patch > systemVersion.patch ) )
                        {
                            throw new IllegalStateException(
                                "Included dependency " + requested.getGroup() + ":" + requested.getName() + ":" + requested.getVersion() +
                                    " is incompatible with app systemVersion " + systemVersion.version );
                        }
                        else
                        {
                            details.useVersion( systemVersion.version );
                        }
                    }
                }
                else
                {
                    throw new IllegalStateException(
                        "Included dependency " + requested.getGroup() + ":" + requested.getName() + " is not a library" );
                }
            }
        } ) );

        addExclude( "org.slf4j", null );
        addExclude( "org.osgi", null );
        addExclude( "com.enonic.xp", "core-api" );
        addExclude( "com.enonic.xp", "script-api" );
        addExclude( "com.enonic.xp", "web-api" );
        addExclude( "com.enonic.xp", "admin-api" );
        addExclude( "com.enonic.xp", "portal-api" );
        addExclude( "com.enonic.xp", "jaxrs-api" );
        addExclude( "com.google.guava", "guava" );
    }

    private void addExclude( final String group, final String module )
    {
        final Map<String, String> map = new HashMap<>();
        if ( group != null )
        {
            map.put( "group", group );
        }

        if ( module != null )
        {
            map.put( "module", module );
        }

        this.config.exclude( map );
    }
}
