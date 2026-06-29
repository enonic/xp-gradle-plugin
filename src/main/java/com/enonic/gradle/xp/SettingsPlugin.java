package com.enonic.gradle.xp;

import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;

public final class SettingsPlugin
    implements Plugin<Settings>
{
    static final String CATALOG_NAME = "xplibs";

    private static final String XP_GROUP = "com.enonic.xp";

    private static final String[] XP_APIS =
        {"core-api", "script-api", "web-api", "admin-api", "portal-api", "jaxrs-api"};

    private static final String[] XP_LIBS =
        {"lib-admin", "lib-app", "lib-auditlog", "lib-auth", "lib-cluster", "lib-common", "lib-content", "lib-context", "lib-event",
            "lib-export", "lib-grid", "lib-i18n", "lib-io", "lib-mail", "lib-node", "lib-portal", "lib-project", "lib-repo",
            "lib-scheduler", "lib-schema", "lib-sse", "lib-task", "lib-value", "lib-vhost", "lib-websocket"};

    @Override
    public void apply( final Settings settings )
    {
        final XpSettingsExtension ext = settings.getExtensions().create( "xp", XpSettingsExtension.class );

        settings.getGradle().settingsEvaluated( evaluated -> {
            final String version = XpVersionResolver.resolveVersion( ext.getVersion().getOrNull(),
                                                                     evaluated.getProviders().gradleProperty( "xpVersion" ).getOrNull() );

            evaluated.dependencyResolutionManagement( drm -> drm.versionCatalogs( container -> {
                container.create( CATALOG_NAME, catalog -> {
                    catalog.version( "xp", version );
                    for ( final String api : XP_APIS )
                    {
                        final String alias = "api-" + api.substring( 0, api.indexOf( '-' ) );
                        catalog.library( alias, XP_GROUP, api ).versionRef( "xp" );
                    }
                    for ( final String lib : XP_LIBS )
                    {
                        final String alias = lib.substring( 4 );
                        catalog.library( alias, XP_GROUP, lib ).versionRef( "xp" );
                    }
                } );
            } ) );
        } );
    }
}
