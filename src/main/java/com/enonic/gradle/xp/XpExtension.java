package com.enonic.gradle.xp;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;

public class XpExtension
{
    private final Project project;

    private final Property<String> version;

    private final Property<File> homeDir;

    private final Property<File> installDir;

    public XpExtension( final Project project )
    {
        this.project = project;
        ObjectFactory objectFactory = project.getObjects();
        this.version = objectFactory.property( String.class );
        this.homeDir = objectFactory.property( File.class );
        this.installDir = objectFactory.property( File.class );
    }

    public String getVersion()
    {
        return this.version.get();
    }

    public Provider<String> getVersionProvider()
    {
        return this.version;
    }
    
    public Property<String> getVersionProperty()
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
    
    public Property<File> getHomeDirProperty()
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
    
    public Property<File> getInstallDirProperty()
    {
        return this.installDir;
    }

    public void setInstallDir( final File dir )
    {
        this.installDir.set( dir );
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
