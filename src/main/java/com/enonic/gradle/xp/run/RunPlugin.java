package com.enonic.gradle.xp.run;

import java.util.Collections;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import com.enonic.gradle.xp.BasePlugin;
import com.enonic.gradle.xp.XpExtension;

public final class RunPlugin
    implements Plugin<Project>, RunConstants
{
    private Project project;

    private XpExtension ext;

    @Override
    public void apply( final Project project )
    {
        this.project = project;
        this.project.getPlugins().apply( BasePlugin.class );

        this.ext = XpExtension.get( this.project );

        createInstallTask();
        createRunTask();
        createDeployAllTask();
        addConfigurations();

        this.project.afterEvaluate( this::configureDependencies );
    }

    private void addConfigurations()
    {
        this.project.getConfigurations().create( APP_CONFIG ).setTransitive( false );
        this.project.getConfigurations().create( DISTRO_CONFIG ).setTransitive( false );
    }

    private void configureDependencies( final Project project )
    {
        project.getDependencies().add( DISTRO_CONFIG, DISTRO_GROUP + ":" + DISTRO_NAME + ":" + this.ext.getVersion() + "@zip" );
    }

    private void createInstallTask()
    {
        this.project.getTasks().create( INSTALL_TASK, InstallTask.class );
    }

    private void createDeployAllTask()
    {
        this.project.getTasks().create( DEPLOY_ALL_TASK, DeployAllTask.class, task ->
        {
            task.setDependsOn( Collections.singleton( INSTALL_TASK ) );
        } );
    }

    private void createRunTask()
    {
        this.project.getTasks().create( RUN_TASK, RunTask.class, task ->
        {
            task.setDependsOn( Collections.singleton( DEPLOY_ALL_TASK ) );
        } );
    }
}
