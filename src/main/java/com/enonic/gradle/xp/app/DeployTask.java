package com.enonic.gradle.xp.app;

import java.io.File;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.CopySpec;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.Jar;

public class DeployTask
    extends DefaultTask
{
    private final Property<File> homeDir;

    public DeployTask()
    {
        setGroup( "Application" );
        setDescription( "Deploy application to XP_HOME directory." );
        dependsOn( getProject().getTasks().getByName( "build" ) );
        this.homeDir = getProject().getObjects().property( File.class );
    }

    @InputFile
    public File getFrom()
    {
        return ( (Jar) getProject().getTasks().getByPath( "jar" ) ).getArchivePath();
    }

    private File resolveHomeDir()
    {
        final File file = this.homeDir.getOrNull();
        return file != null ? file : getProject().getBuildDir();
    }

    @OutputDirectory
    public File getDeployDir()
    {
        return new File( resolveHomeDir(), "deploy" );
    }

    public void setHomeDir( final Provider<File> dir )
    {
        this.homeDir.set( dir );
    }

    @TaskAction
    public void run()
    {
        getProject().copy( this::doCopy );
    }

    private void doCopy( final CopySpec spec )
    {
        spec.from( getFrom() );
        spec.into( getDeployDir() );
    }
}
