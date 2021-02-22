package com.enonic.gradle.xp.app;

import java.io.File;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

public class UnpackWebJarTask
    extends DefaultTask
{
    public UnpackWebJarTask()
    {
        setGroup( "Application" );
        setDescription( "Unpack all webjars into temporary directory." );
    }

    @InputFiles
    public FileTree getFrom()
    {
        final Configuration config = getProject().getConfigurations().getByName( "webjar" );
        return config.getAsFileTree();
    }

    @OutputDirectory
    public File getOutputDir()
    {
        return new File( getProject().getBuildDir(), "webjars" );
    }

    @TaskAction
    public void run()
    {
        getProject().copy( this::doCopy );
    }

    private void doCopy( final CopySpec spec )
    {
        for ( final File dependency : getFrom() )
        {
            spec.from( getProject().zipTree( dependency ) );
        }

        spec.into( getOutputDir() );
    }
}
