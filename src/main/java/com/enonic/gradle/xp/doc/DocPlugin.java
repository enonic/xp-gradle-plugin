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
    implements Plugin<Project>
{
    private final static String ASCIIDOCTOR_TASK = "asciidoctor";

    private Project project;

    @Override
    public void apply( final Project project )
    {
        this.project = project;

        this.project.getPlugins().apply( BasePlugin.class );
        this.project.getPlugins().apply( AsciidoctorPlugin.class );

        configureAsciidoctor();
        createDocTask();
    }

    private void createDocTask()
    {
        final Task task = this.project.getTasks().create( "doc" );
        task.setGroup( "Documentation" );
        task.setDescription( "Generates documentation based on Asciidoctor (alias for " + ASCIIDOCTOR_TASK + ")." );
        task.dependsOn( ASCIIDOCTOR_TASK );
    }

    private void configureAsciidoctor()
    {
        final AsciidoctorTask task = (AsciidoctorTask) this.project.getTasks().getByName( ASCIIDOCTOR_TASK );
        task.setSourceDir( new File( this.project.getProjectDir(), "docs" ) );
        task.setBackends( "html5" );
        task.setOutputDir( this.project.getBuildDir() );

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

        task.setAttributes( attributes );
    }
}
