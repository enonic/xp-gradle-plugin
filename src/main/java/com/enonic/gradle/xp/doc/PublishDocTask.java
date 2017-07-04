package com.enonic.gradle.xp.doc;

import java.io.File;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class PublishDocTask
    extends DefaultTask
    implements DocConstants
{
    private final DocExtension ext;

    private File sourceDir;

    public PublishDocTask()
    {
        setGroup( GROUP );
        setDescription( "Publish documentation to S3." );
        dependsOn( ASCIIDOCTOR_TASK );

        this.ext = DocExtension.get( getProject() );
    }

    public void setSourceDir( final File sourceDir )
    {
        this.sourceDir = sourceDir;
    }

    @TaskAction
    public void publish()
        throws Exception
    {
        final S3Copy copy = new S3Copy( getProject(), this.ext.getS3() );
        copy.setDeleteAll( true );
        copy.setSourceDir( this.sourceDir );

        final String name = getProject().getName();
        final Object group = getProject().getGroup();

        String targetPath = name;
        if ( group != null )
        {
            targetPath = group.toString() + "/" + targetPath;
        }

        if ( this.ext.getVersion() != null )
        {
            targetPath += "/" + this.ext.getVersion();
        }

        copy.setTargetPath( targetPath );
        copy.copy();
    }
}
