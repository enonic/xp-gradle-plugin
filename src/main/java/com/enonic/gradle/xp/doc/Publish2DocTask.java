package com.enonic.gradle.xp.doc;

import java.io.File;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class Publish2DocTask
    extends DefaultTask
    implements DocConstants
{
    private final DocExtension ext;

    private File zipFile;

    public Publish2DocTask()
    {
        setGroup( GROUP );
        setDescription( "Publish documentation to DocPortal." );
        dependsOn( ZIP_DOC_TASK );

        this.ext = DocExtension.get( getProject() );
    }

    public void setZipFile( final File zipFile )
    {
        this.zipFile = zipFile;
    }

    @TaskAction
    public void publish()
        throws Exception
    {
        final UploadTask task = new UploadTask( getProject(), this.ext );
        task.setZipFile( this.zipFile );

        task.upload();
    }
}
