package com.enonic.gradle.xp.app;

import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.bundling.Jar;

import aQute.bnd.gradle.BndBuilderPlugin;
import aQute.bnd.gradle.BundleTaskConvention;

import com.enonic.gradle.xp.BasePlugin;
import com.enonic.gradle.xp.XpExtension;

public final class AppPlugin
    implements Plugin<Project>
{
    private Project project;

    private XpExtension ext;

    private AppExtension appExt;

    @Override
    public void apply( final Project project )
    {
        this.project = project;

        this.project.getPlugins().apply( BasePlugin.class );
        this.project.getPlugins().apply( JavaPlugin.class );
        this.project.getPlugins().apply( BndBuilderPlugin.class );

        this.ext = XpExtension.get( this.project );
        this.appExt = AppExtension.create( this.project );

        this.project.afterEvaluate( this::afterEvaluate );

        addLibraryConfig();
        addWebJarConfig();
        applyDeployTask();
        applyUnpackWebJarTask();
    }

    private void afterEvaluate( final Project project )
    {
        final Jar jar = (Jar) project.getTasks().getByName( "jar" );
        final BundleTaskConvention ext = (BundleTaskConvention) jar.getConvention().getPlugins().get( "bundle" );

        new BundleConfigurator( project, ext ).configure( this.appExt );
        if ( !appExt.isKeepArchiveFileName() )
        {
            skipJarVersion();
        }
    }

    private void applyDeployTask()
    {
        this.project.getTasks().create( "deploy", DeployTask.class );
        this.project.getTasks().withType( DeployTask.class, task -> task.setHomeDir( ext.getHomeDirProvider() ) );
    }

    private void addLibraryConfig()
    {
        final Configuration libConfig = this.project.getConfigurations().create( "include", conf -> conf.setTransitive( true ) );
        this.project.getConfigurations().getByName( "compile" ).extendsFrom( libConfig );
    }

    private void addWebJarConfig()
    {
        this.project.getConfigurations().create( "webjar", conf -> conf.setTransitive( true ) );
    }

    private void applyUnpackWebJarTask()
    {
        final UnpackWebJarTask task = this.project.getTasks().create( "unpackWebJars", UnpackWebJarTask.class );
        this.project.getTasks().getByName( "jar" ).dependsOn( task );
    }

    private void skipJarVersion()
    {
        final Jar jar = (Jar) project.getTasks().getByName( "jar" );
        final String base = jar.getArchiveBaseName().getOrElse( "" );
        final String appendix = jar.getArchiveAppendix().getOrElse( "" );
        final String classifier = jar.getArchiveClassifier().getOrElse( "" );
        final String ext = jar.getArchiveExtension().getOrElse( "" );
        final String jarWithoutExt = Stream.of( base, appendix, classifier ).
            filter( Predicate.not( String::isEmpty ) ).
            collect( Collectors.joining( "-" ) );
        jar.getArchiveFileName().set( String.join( ".", jarWithoutExt, ext ) );
    }
}
