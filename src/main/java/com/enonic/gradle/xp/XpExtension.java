package com.enonic.gradle.xp;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.provider.PropertyState;
import org.gradle.api.provider.Provider;

public class XpExtension
{
    private final Project project;

    private final PropertyState<String> version;

    private final PropertyState<File> homeDir;

    private final PropertyState<File> installDir;

    private String repoUrl;

    private String repoUser;

    private String repoPassword;

    public XpExtension( final Project project )
    {
        this.project = project;
        this.version = this.project.property( String.class );
        this.homeDir = this.project.property( File.class );
        this.installDir = this.project.property( File.class );
        this.repoUser = findProperty("repoUser");
        this.repoPassword = findProperty("repoPassword");
    }

    private String findProperty( final String name )
    {
        final Object value = this.project.findProperty( name );
        return value != null ? value.toString() : null;
    }

    public String getVersion()
    {
        return this.version.get();
    }

    public Provider<String> getVersionProvider()
    {
        return this.version;
    }

    public void setVersion( final String version )
    {
        this.version.set( version );
    }

    public File getHomeDir()
    {
        return this.homeDir.get();
    }

    public Provider<File> getHomeDirProvider()
    {
        return this.homeDir;
    }

    public void setHomeDir( final File dir )
    {
        this.homeDir.set( dir );
    }

    public File getInstallDir()
    {
        return this.installDir.get();
    }

    public Provider<File> getInstallDirProvider()
    {
        return this.installDir;
    }

    public void setInstallDir( final File dir )
    {
        this.installDir.set( dir );
    }

    public String getRepoUrl()
    {
        return this.repoUrl;
    }

    public void setRepoUrl( final String url )
    {
        this.repoUrl = url;
    }

    public String getRepoUser()
    {
        return this.repoUser;
    }

    public void setRepoUser( final String repoUser )
    {
        this.repoUser = repoUser;
    }

    public String getRepoPassword()
    {
        return this.repoPassword;
    }

    public void setRepoPassword( final String repoPassword )
    {
        this.repoPassword = repoPassword;
    }

    public static XpExtension get( final Project project )
    {
        return project.getExtensions().getByType( XpExtension.class );
    }

    public static XpExtension create( final Project project )
    {
        return project.getExtensions().create( "xp", XpExtension.class, project );
    }

    public MavenArtifactRepository enonicRepo()
    {
        return enonicRepo( "public" );
    }

    public MavenArtifactRepository enonicRepo( final String name )
    {
        return this.project.getRepositories().maven( repo -> repo.setUrl( "https://repo.enonic.com/" + name ) );
    }
}
