package com.enonic.gradle.xp.doc;

import java.io.File;
import java.util.Map;

import org.asciidoctor.gradle.AsciidoctorPlugin;
import org.asciidoctor.gradle.AsciidoctorTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

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
        createPublishDocTask();
        configureAsciidoctor();
    }

    private void createBuildDocTask()
    {
        final Task task = this.project.getTasks().create( "buildDoc" );
        task.setGroup( GROUP );
        task.setDescription( "Build documentation based on Asciidoctor (alias for " + ASCIIDOCTOR_TASK + ")." );
        task.dependsOn( ASCIIDOCTOR_TASK );
    }

    private void createPublishDocTask()
    {
        final PublishDocTask task = this.project.getTasks().create( "publishDoc", PublishDocTask.class );
        task.setGroup( GROUP );
        task.setDescription( "Publish documentation to S3." );
        task.dependsOn( ASCIIDOCTOR_TASK );
        task.setSourceDir( new File( getDocsOutputDir(), "html5" ) );

        this.project.afterEvaluate( project -> task.setEnabled( isS3Enabled() ) );
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
