package com.enonic.gradle.xp.run;

import java.io.File;
import java.util.Set;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.CopySpec;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import com.enonic.gradle.xp.XpExtension;

public class DeployAllTask
    extends DefaultTask
    implements RunConstants
{
    private final XpExtension ext;

    public DeployAllTask()
    {
        setGroup( GROUP );
        setDescription( "Deploy all apps defined as dependencies." );

        this.ext = XpExtension.get( getProject() );
    }

    @OutputDirectory
    public File getDeployDir()
    {
        return new File( this.ext.getHomeDir(), "deploy" );
    }

    @Input
    public Set<File> getFiles()
    {
        return getProject().getConfigurations().getByName( APP_CONFIG ).resolve();
    }

    @TaskAction
    public void action()
    {
        getProject().copy( this::configureCopySpec );
    }

    private void configureCopySpec( final CopySpec spec )
    {
        spec.into( getDeployDir() );
        spec.from( getFiles() );
        spec.setIncludeEmptyDirs( false );
    }
}
