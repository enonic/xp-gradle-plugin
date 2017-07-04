package com.enonic.gradle.xp.run;

import java.io.File;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.FileCopyDetails;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import com.enonic.gradle.xp.XpExtension;

public class InstallTask
    extends DefaultTask
    implements RunConstants
{
    private final XpExtension ext;

    public InstallTask()
    {
        setGroup( GROUP );
        setDescription( "Download and install Enonic XP." );

        this.ext = XpExtension.get( getProject() );
    }

    @Input
    public String getVersion()
    {
        return this.ext.getVersion();
    }

    @OutputDirectory
    public File getInstallDir()
    {
        return this.ext.getInstallDir();
    }

    @TaskAction
    public void action()
    {
        getProject().delete( getInstallDir() );
        getProject().copy( this::configureCopySpec );
    }

    private void configureCopySpec( final CopySpec spec )
    {
        spec.into( getInstallDir() );
        spec.from( getProject().zipTree( resolveDependency() ) );
        spec.setIncludeEmptyDirs( false );
        spec.eachFile( this::configureRename );
    }

    private void configureRename( final FileCopyDetails details )
    {
        final String newPath = details.getPath().substring( details.getPath().indexOf( '/' ) + 1 );
        details.setPath( newPath );
    }

    private File resolveDependency()
    {
        return getProject().getConfigurations().getByName( DISTRO_CONFIG ).resolve().iterator().next();
    }
}
