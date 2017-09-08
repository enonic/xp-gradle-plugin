package com.enonic.gradle.xp.doc;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.util.ConfigureUtil;

import groovy.lang.Closure;

public class DocExtension
{
    private String version;

    private final S3Settings s3Settings;

    private final UploadSettings uploadSettings;

    public DocExtension()
    {
        this.s3Settings = new S3Settings();
        this.uploadSettings = new UploadSettings();
    }

    public String getVersion()
    {
        return this.version;
    }

    public void setVersion( final String version )
    {
        this.version = version;
    }

    public S3Settings getS3()
    {
        return this.s3Settings;
    }

    public void s3( final Closure settings )
    {
        s3( ConfigureUtil.configureUsing( settings ) );
    }

    public void s3( final Action<S3Settings> settings )
    {
        settings.execute( this.s3Settings );
    }

    public UploadSettings getUpload()
    {
        return this.uploadSettings;
    }

    public void upload( final Closure settings )
    {
        upload( ConfigureUtil.configureUsing( settings ) );
    }

    public void upload( final Action<UploadSettings> settings )
    {
        settings.execute( this.uploadSettings );
    }

    public static DocExtension get( final Project project )
    {
        return project.getExtensions().getByType( DocExtension.class );
    }

    public static DocExtension create( final Project project )
    {
        return project.getExtensions().create( "doc", DocExtension.class );
    }
}
