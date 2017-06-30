package com.enonic.gradle.xp.doc;

public final class DocPlugin
    // extends BasePlugin
{
    /*
    private final static String ASCIIDOCTOR_TASK = "asciidoctor";

    @Override
    protected void configure()
    {
        this.project.getPlugins().apply( AsciidoctorPlugin.class );

        configureAsciidoctor();
        createDocTask();
        createDocZipTask();
    }

    private void createDocTask()
    {
        this.project.getTasks().create( "doc", task ->
        {
            task.setGroup( "xp" );
            task.setDescription( "Generates documentation based on Asciidoctor (alias for " + ASCIIDOCTOR_TASK + ")." );
            task.dependsOn( ASCIIDOCTOR_TASK );
        } );
    }

    private void createDocZipTask()
    {
        final Zip zipTask = this.project.getTasks().create( "docZip", Zip.class, task ->
        {
            task.setGroup( "xp" );
            task.setDescription( "Zips up the documentation." );
            task.dependsOn( "doc" );

            task.setClassifier( "doc" );
            task.from( new File( this.project.getBuildDir(), "html5" ) );
        } );

        this.project.getArtifacts().add( "archives", zipTask );
    }

    private void configureAsciidoctor()
    {
        final AsciidoctorTask task = (AsciidoctorTask) this.project.getTasks().getByName( ASCIIDOCTOR_TASK );
        task.setSourceDir( new File( this.project.getProjectDir(), "docs" ) );
        task.setOutputDir( this.project.getBuildDir() );
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

        task.setAttributes( attributes );
    }
    */
}
