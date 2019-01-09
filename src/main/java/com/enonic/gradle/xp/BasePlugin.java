package com.enonic.gradle.xp;

import java.io.File;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;

public final class BasePlugin
    implements Plugin<Project>
{
    @Override
    public void apply( final Project project )
    {
        final XpExtension ext = XpExtension.create( project );
        ext.setVersion( "7.+" );
        ext.setInstallDir( new File( project.getBuildDir(), "xp" ) );
        ext.setHomeDir( findHomeDir( ext.getInstallDir(), project.findProperty( "xpHome" ) ) );

        applyJavaBased( project );
    }

    private void applyJavaBased( final Project project )
    {
        if ( project.getPlugins().hasPlugin( JavaPlugin.class ) )
        {
            createUnpackDevTask( project );
        }
        else
        {
            project.getPlugins().withType( JavaPlugin.class ).whenPluginAdded( plugin -> createUnpackDevTask( project ) );
        }
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

    private void createUnpackDevTask( final Project project )
    {
        project.getTasks().create( "unpackDevResources", UnpackDevTask.class );
    }
}
