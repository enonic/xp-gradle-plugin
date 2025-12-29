package com.enonic.gradle.xp;

import java.io.File;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;

public final class BasePlugin
    implements Plugin<Project>
{
    @Override
    public void apply( final Project project )
    {
        final XpExtension ext = XpExtension.create( project );
        ext.setVersion( "8.+" );
        ext.getInstallDirProperty().set( project.getLayout().getBuildDirectory().dir( "xp" ).map( Directory::getAsFile ) );
        ext.getHomeDirProperty().set( ext.getInstallDirProperty().map( installDir -> findHomeDir( installDir, project.findProperty( "xpHome" ) ) ) );
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
}
