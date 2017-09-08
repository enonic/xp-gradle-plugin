package com.enonic.gradle.xp;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.CopySpec;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

public class UnpackDevTask
    extends DefaultTask
{
    @TaskAction
    public void run()
    {
        getProject().copy( this::copySpec );
    }

    @InputFiles
    public Set<File> getInputFiles()
    {
        try
        {
            final Configuration config = getProject().getConfigurations().getByName( "compile" );
            return config.getFiles();
        }
        catch ( final Exception e )
        {
            return new HashSet<>();
        }
    }

    @OutputDirectory
    public File getOutputDir()
    {
        return new File( getProject().getProjectDir(), ".xp" );
    }

    private void copySpec( final CopySpec spec )
    {
        getInputFiles().forEach( f -> spec.from( getProject().zipTree( f ) ) );
        spec.include( "dev/**" );
        spec.into( getOutputDir() );
    }
}
