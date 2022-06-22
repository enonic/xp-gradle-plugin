package com.enonic.gradle.xp.app;

import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository;
import org.gradle.api.tasks.bundling.Jar;

import aQute.bnd.gradle.BndBuilderPlugin;

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
    }

    private void afterEvaluate( final Project project )
    {
        final boolean hasSourcePaths = new BundleConfigurator( project ).configure( this.appExt );

        if ( !appExt.isKeepArchiveFileName() )
        {
            skipJarVersion();
        }
        if ( !appExt.isAllowDevSourcePathsPublishing() && hasSourcePaths )
        {
            preventSourcePathsPublishing();
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
        this.project.getConfigurations().getByName( "implementation" ).extendsFrom( libConfig );
    }

    private void addWebJarConfig()
    {
        this.project.getConfigurations().create( "webjar", conf -> conf.setTransitive( true ) );
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

    private void preventSourcePathsPublishing()
    {
        this.project.getTasks().withType( PublishToMavenRepository.class ).all( task -> task.doFirst( t -> {
            throw new IllegalStateException( "Application has non-empty X-Source-Paths. " +
                                                 "Build application with com.enonic.xp.app.production property to true for publishing." );
        } ) );
    }
}
