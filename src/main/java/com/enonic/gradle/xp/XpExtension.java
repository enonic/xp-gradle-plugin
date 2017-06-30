package com.enonic.gradle.xp;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.provider.PropertyState;
import org.gradle.api.provider.Provider;

public class XpExtension
{
    private final PropertyState<String> version;

    private final PropertyState<File> homeDir;

    private final PropertyState<File> installDir;

    public XpExtension( final Project project )
    {
        this.version = project.property( String.class );
        this.homeDir = project.property( File.class );
        this.installDir = project.property( File.class );
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

    public static XpExtension get( final Project project )
    {
        return project.getExtensions().getByType( XpExtension.class );
    }

    public static XpExtension create( final Project project )
    {
        return project.getExtensions().create( "xp", XpExtension.class, project );
    }
}
