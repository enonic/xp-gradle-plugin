package com.enonic.gradle.xp.app;

import java.util.Objects;
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
        applyDevTask();
    }

    private void afterEvaluate( final Project project )
    {
        final XpVersion xpVersion = getXpVersion();
        final boolean hasSourcePaths = new BundleConfigurator( project ).configure( this.appExt, xpVersion );

        if ( !appExt.isKeepArchiveFileName() )
        {
            skipJarVersion();
        }
        if ( !appExt.isAllowDevSourcePathsPublishing() && hasSourcePaths )
        {
            preventSourcePathsPublishing();
        }

        ensureCorrectJavaCompilerVersion( project, xpVersion );
    }

    private XpVersion getXpVersion()
    {
        final String version = Objects.requireNonNullElse( appExt.getSystemVersion(), "" ).trim();
        if ( version.isEmpty() )
        {
            throw new IllegalArgumentException(
                "XP system version not specified. Please add the following line in the 'app' closure in build.gradle:\r\n  systemVersion = \"${xpVersion}\"" );
        }
        final XpVersion xpVersion = XpVersion.parse( version );
        if ( !xpVersion.valid )
        {
            throw new IllegalArgumentException( "Invalid XP system version: systemVersion = '" + version + "'" );
        }
        return xpVersion;
    }

    private void applyDeployTask()
    {
        project.getTasks().register( "deploy", DeployTask.class );
        project.getTasks().withType( DeployTask.class, task -> {
            task.setHomeDir( ext.getHomeDirProperty() );
            task.getFrom().set( project.getTasks().named( "jar", Jar.class ).flatMap( Jar::getArchiveFile ) );
        } );
    }

    private void applyDevTask()
    {
        project.afterEvaluate( p -> {
            if ( appExt.isCreateDefaultDevTask() )
            {
                p.getTasks().register( "dev", DevTask.class, task -> {
                    final String taskName = appExt.getContinuousTaskName();
                    final String projectPath = p.getPath();
                    final String qualifiedTaskName = ":".equals( projectPath ) ? taskName : projectPath + ":" + taskName;
                    task.getContinuousTaskName().set( qualifiedTaskName );
                } );
            }
        } );
    }

    private void addLibraryConfig()
    {
        final Configuration libConfig = project.getConfigurations().create( "include", conf -> conf.setTransitive( true ) );
        project.getConfigurations().getByName( "implementation" ).extendsFrom( libConfig );
    }

    private void addWebJarConfig()
    {
        project.getConfigurations().create( "webjar", conf -> conf.setTransitive( true ) );
    }

    private void skipJarVersion()
    {
        final Jar jar = (Jar) project.getTasks().getByName( "jar" );
        final String base = jar.getArchiveBaseName().getOrElse( "" );
        final String appendix = jar.getArchiveAppendix().getOrElse( "" );
        final String classifier = jar.getArchiveClassifier().getOrElse( "" );
        final String ext = jar.getArchiveExtension().getOrElse( "" );
        final String jarWithoutExt =
            Stream.of( base, appendix, classifier ).filter( Predicate.not( String::isEmpty ) ).collect( Collectors.joining( "-" ) );
        jar.getArchiveFileName().set( String.join( ".", jarWithoutExt, ext ) );
    }

    private void preventSourcePathsPublishing()
    {
        project.getTasks().withType( PublishToMavenRepository.class ).all( task -> task.doFirst( t -> {
            throw new IllegalStateException( "Application has non-empty X-Source-Paths. " +
                                                 "Build application without -Pdev property for publishing." );
        } ) );
    }

    static void ensureCorrectJavaCompilerVersion( Project project, final XpVersion xpVersion )
    {
        if ( xpVersion.major < 8 )
        {
            throw new IllegalStateException( "XP below version 8.0 are not supported" );
        }
    }
}
