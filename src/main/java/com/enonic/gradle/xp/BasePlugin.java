package com.enonic.gradle.xp;

import java.io.File;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.plugins.MavenPlugin;
import org.gradle.api.tasks.Upload;

public final class BasePlugin
    implements Plugin<Project>
{
    private final static String PUBLIC_REPO = "https://repo.enonic.com/public";

    private Project project;

    private XpExtension ext;

    @Override
    public void apply( final Project project )
    {
        this.project = project;

        this.ext = XpExtension.create( this.project );
        this.ext.setVersion( "6.+" );
        this.ext.setInstallDir( new File( this.project.getBuildDir(), "xp" ) );
        this.ext.setHomeDir( findHomeDir( this.ext.getInstallDir(), this.project.findProperty( "xpHome" ) ) );
        this.ext.setRepoUrl( PUBLIC_REPO );

        this.project.getRepositories().maven( repo -> repo.setUrl( PUBLIC_REPO ) );
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
        task.getRepositories().maven( this::configureUploadRepo );
    }

    private void configureUploadRepo( final MavenArtifactRepository repo )
    {
        repo.setUrl( this.ext.getRepoUrl() );
        repo.getCredentials().setUsername( this.ext.getRepoUser() );
        repo.getCredentials().setPassword( this.ext.getRepoPassword() );
    }
}
