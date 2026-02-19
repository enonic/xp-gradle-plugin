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
        ext.getHomeDirProperty().set(
            project.getProviders().gradleProperty( "xpHome" )
                .orElse( project.getProviders().systemProperty( "xp.home" ) )
                .orElse( project.getProviders().environmentVariable( "XP_HOME" ) )
                .map( File::new )
                .orElse( ext.getInstallDirProperty().map( installDir -> new File( installDir, "home" ) ) )
        );
    }
}