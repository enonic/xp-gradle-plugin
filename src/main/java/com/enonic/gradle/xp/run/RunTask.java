package com.enonic.gradle.xp.run;

import java.io.File;

import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskAction;

import com.enonic.gradle.xp.XpExtension;

public class RunTask
    extends JavaExec
    implements RunConstants
{
    private final XpExtension ext;

    private boolean devMode;

    public RunTask()
    {
        setGroup( GROUP );
        setDescription( "Starts up an instance of Enonic XP." );

        this.ext = XpExtension.get( getProject() );
        this.devMode = false;
    }

    @Override
    @TaskAction
    public void exec()
    {
        classpath( findClassPath() );
        setMain( "com.enonic.xp.launcher.LauncherMain" );

        if ( this.devMode )
        {
            args( "dev" );
        }

        systemProperty( "xp.install", this.ext.getInstallDir().getAbsolutePath() );
        systemProperty( "xp.home", this.ext.getHomeDir().getAbsolutePath() );

        super.exec();
    }

    private FileTree findClassPath()
    {
        return getProject().fileTree( new File( this.ext.getInstallDir(), "lib" ) );
    }

    public void setDevMode( final boolean devMode )
    {
        this.devMode = devMode;
    }
}
