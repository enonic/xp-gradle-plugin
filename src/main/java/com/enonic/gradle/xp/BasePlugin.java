package com.enonic.gradle.xp;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

public final class BasePlugin
    implements Plugin<Project>
{
    @Override
    public void apply( final Project project )
    {
        XpExtension.create( project );

        project.getPlugins().withType( JavaPlugin.class, javaPlugin -> {
            final JavaPluginExtension javaExt = project.getExtensions().getByType( JavaPluginExtension.class );
            javaExt.getToolchain().getLanguageVersion().convention( JavaLanguageVersion.of( 25 ) );
        } );
    }
}