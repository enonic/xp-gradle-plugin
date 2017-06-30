package com.enonic.gradle.xp.app;

import org.dm.gradle.plugins.bundle.BundleExtension;
import org.dm.gradle.plugins.bundle.BundlePlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPlugin;

import com.enonic.gradle.xp.BasePlugin;
import com.enonic.gradle.xp.XpExtension;

public final class AppPlugin
    implements Plugin<Project>
{
    private Project project;

    private XpExtension ext;

    private AppExtension appExt;

    private BundleExtension bundleExt;

    @Override
    public void apply( final Project project )
    {
        this.project = project;

        this.project.getPlugins().apply( BasePlugin.class );
        this.project.getPlugins().apply( JavaPlugin.class );
        this.project.getPlugins().apply( BundlePlugin.class );

        this.ext = XpExtension.get( this.project );
        this.bundleExt = this.project.getExtensions().getByType( BundleExtension.class );
        this.appExt = AppExtension.create( this.project );

        this.project.afterEvaluate( this::afterEvaluate );

        addLibraryConfig();
        addWebJarConfig();
        applyDeployTask();
        applyUnpackWebJarTask();
    }

    private void afterEvaluate( final Project project )
    {
        new BundleConfigurator( project, this.bundleExt ).configure( this.appExt );
    }

    private void applyDeployTask()
    {
        this.project.getTasks().create( "deploy", DeployTask.class );
        this.project.getTasks().withType( DeployTask.class, task -> task.setHomeDir( ext.getHomeDirProvider() ) );
    }

    private void addLibraryConfig()
    {
        final Configuration libConfig = this.project.getConfigurations().create( "include", conf -> conf.setTransitive( true ) );
        this.project.getConfigurations().getByName( "compile" ).extendsFrom( libConfig );
    }

    private void addWebJarConfig()
    {
        this.project.getConfigurations().create( "webjar", conf -> conf.setTransitive( true ) );
    }

    private void applyUnpackWebJarTask()
    {
        final UnpackWebJarTask task = this.project.getTasks().create( "unpackWebJars", UnpackWebJarTask.class );
        this.project.getTasks().getByName( "jar" ).dependsOn( task );
    }
}
