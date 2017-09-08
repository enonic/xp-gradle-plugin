package com.enonic.gradle.xp.doc;

import java.io.File;
import java.util.Map;

import org.asciidoctor.gradle.AsciidoctorPlugin;
import org.asciidoctor.gradle.AsciidoctorTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.bundling.Zip;

import com.google.common.collect.Maps;

import com.enonic.gradle.xp.BasePlugin;

public class DocPlugin
    implements Plugin<Project>, DocConstants
{
    private Project project;

    private DocExtension ext;

    @Override
    public void apply( final Project project )
    {
        this.project = project;
        this.ext = DocExtension.create( this.project );

        this.project.getPlugins().apply( BasePlugin.class );
        this.project.getPlugins().apply( AsciidoctorPlugin.class );

        createBuildDocTask();
        createZipDocTask();
        createPublishDocTask();
        createPublish2DocTask();
        configureAsciidoctor();
    }

    private void createBuildDocTask()
    {
        final Task task = this.project.getTasks().create( BUILD_DOC_TASK );
        task.setGroup( GROUP );
        task.setDescription( "Build documentation based on Asciidoctor (alias for " + ASCIIDOCTOR_TASK + ")." );
        task.dependsOn( ASCIIDOCTOR_TASK );
    }

    private void createZipDocTask()
    {
        final Zip task = this.project.getTasks().create( ZIP_DOC_TASK, Zip.class );
        task.from( new File( getDocsOutputDir(), "html5" ) );
        task.setClassifier( "doc" );
        task.setGroup( GROUP );
        task.setDescription( "Create a zip archive of the documentation." );
        task.dependsOn( BUILD_DOC_TASK );
    }

    private void createPublishDocTask()
    {
        final PublishDocTask task = this.project.getTasks().create( "publishDoc", PublishDocTask.class );
        task.setGroup( GROUP );
        task.setSourceDir( new File( getDocsOutputDir(), "html5" ) );

        this.project.afterEvaluate( project -> task.setEnabled( isS3Enabled() ) );
    }

    private void createPublish2DocTask()
    {
        final Publish2DocTask task = this.project.getTasks().create( "publish2Doc", Publish2DocTask.class );
        task.setGroup( GROUP );
        task.setZipFile( ( (Zip) this.project.getTasks().getByName( ZIP_DOC_TASK ) ).getArchivePath() );
    }

    private boolean isS3Enabled()
    {
        final S3Settings settings = this.ext.getS3();
        return settings != null && settings.getBucketName() != null;
    }

    private File getDocsOutputDir()
    {
        return new File( this.project.getBuildDir(), "docs" );
    }

    private void configureAsciidoctor()
    {
        final AsciidoctorTask task = (AsciidoctorTask) this.project.getTasks().getByName( ASCIIDOCTOR_TASK );
        task.setGroup( GROUP );

        task.setSourceDir( new File( this.project.getProjectDir(), "docs" ) );
        task.setOutputDir( getDocsOutputDir() );
        task.setBackends( "html5" );

        final Map<String, Object> attributes = Maps.newHashMap();
        attributes.put( "icons", "font" );
        attributes.put( "sectanchors", true );
        attributes.put( "sectlinks", true );
        attributes.put( "linkattrs", true );
        attributes.put( "encoding", "utf-8" );
        attributes.put( "idprefix", "" );
        attributes.put( "toc", "right" );
        attributes.put( "toclevels", 2 );
        attributes.put( "nofooter", "" );
        attributes.put( "source-highlighter", "coderay" );
        attributes.put( "coderay-linenums-mode", "table" );
        attributes.put( "version", this.project.getVersion() );

        task.setAttributes( attributes );
    }
}
