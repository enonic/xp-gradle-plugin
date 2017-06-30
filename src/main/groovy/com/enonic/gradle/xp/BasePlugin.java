package com.enonic.gradle.xp;

import java.io.File;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.MavenPlugin;
import org.gradle.api.tasks.Upload;

import com.enonic.gradle.xp.repo.RepoHelper;

public final class BasePlugin
    implements Plugin<Project>
{
    private final static String PUBLIC_REPO = "https://repo.enonic.com/public";

    private Project project;

    @Override
    public void apply( final Project project )
    {
        this.project = project;

        final XpExtension ext = XpExtension.create( this.project );
        ext.setVersion( "6.+" );
        ext.setInstallDir( new File( this.project.getBuildDir(), "xp" ) );
        ext.setHomeDir( findHomeDir( ext.getInstallDir(), this.project.findProperty( "xpHome" ) ) );
        ext.setRepoUrl( PUBLIC_REPO );
        this.project.getPlugins().withType( MavenPlugin.class, this::configureMavenPlugin );
    }

    private static File findHomeDir( final File installDir, final Object xpHomeProp )
    {
        if ( xpHomeProp != null )
        {
            return new File( xpHomeProp.toString() );
        }

        final String propValue = System.getProperty( "xp.home" );
        if ( propValue != null )
        {
            return new File( propValue );
        }

        final String envValue = System.getenv( "XP_HOME" );
        if ( envValue != null )
        {
            return new File( envValue );
        }

        return new File( installDir, "home" );
    }

    private void configureMavenPlugin( final MavenPlugin plugin )
    {
        this.project.getTasks().withType( Upload.class, this::configureUploadTask );
    }

    private void configureUploadTask( final Upload task )
    {
        final RepoHelper helper = new RepoHelper( this.project );
        helper.addMavenDeployer( task.getRepositories() );
    }
}
