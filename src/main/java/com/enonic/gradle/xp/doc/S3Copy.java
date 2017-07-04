package com.enonic.gradle.xp.doc;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.gradle.api.Project;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.DeleteError;
import io.minio.messages.Item;

final class S3Copy
{
    private final Project project;

    private final S3Settings settings;

    private File sourceDir;

    private boolean deleteAll;

    private String targetPath;

    private MinioClient client;

    public S3Copy( final Project project, final S3Settings settings )
    {
        this.project = project;
        this.settings = settings;
        this.targetPath = "/";
        this.deleteAll = true;
    }

    public void setDeleteAll( final boolean deleteAll )
    {
        this.deleteAll = deleteAll;
    }

    public void setTargetPath( final String targetPath )
    {
        this.targetPath = targetPath;
    }

    public void setSourceDir( final File sourceDir )
    {
        this.sourceDir = sourceDir;
    }

    private void connect()
        throws Exception
    {
        this.client = new MinioClient( this.settings.getEndpoint(), this.settings.getAccessKey(), this.settings.getSecretKey() );
    }

    public void copy()
        throws Exception
    {
        connect();
        deleteIfNeeded();
        copyAll();
    }

    private void deleteIfNeeded()
        throws Exception
    {
        if ( !this.deleteAll )
        {
            return;
        }

        final List<String> names = StreamSupport.stream( listObjects().spliterator(), false ).
            map( this::toObjectName ).
            filter( Objects::nonNull ).
            collect( Collectors.toList() );

        names.forEach( it -> this.project.getLogger().info( "Deleting [" + it + "] from bucket [" + this.settings.getBucketName() + "]" ) );
        for ( final Result<DeleteError> error : this.client.removeObject( this.settings.getBucketName(), names ) )
        {
            this.project.getLogger().error( error.get().getString() );
        }
    }

    private Iterable<Result<Item>> listObjects()
        throws Exception
    {
        return this.client.listObjects( this.settings.getBucketName(), normalizePath( this.targetPath ) );
    }

    private String toObjectName( final Result<Item> result )
    {
        try
        {
            return result.get().objectName();
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    private void copyAll()
        throws Exception
    {
        if ( this.sourceDir.isDirectory() )
        {
            copyDir( this.sourceDir, this.targetPath );
        }
        else
        {
            copySingle( this.sourceDir, this.targetPath );
        }
    }

    private void copySingle( final File source, final String targetDir )
        throws Exception
    {
        if ( source.isDirectory() )
        {
            copyDir( source, targetDir + "/" + source.getName() );
            return;
        }

        final String objectName = normalizePath( targetDir + "/" + source.getName() );
        final FileTypeMap map = MimetypesFileTypeMap.getDefaultFileTypeMap();

        final String contentType = map.getContentType( source );

        try (final FileInputStream in = new FileInputStream( source ))
        {
            this.client.putObject( this.settings.getBucketName(), objectName, in, contentType );
            this.project.getLogger().info(
                "Copying " + source.getName() + " to bucket [" + this.settings.getBucketName() + "] object-name [" + objectName +
                    "] with content-type [" + contentType + "]" );
        }
    }

    private void copyDir( final File source, final String targetDir )
        throws Exception
    {
        final File[] files = source.listFiles();
        if ( files == null )
        {
            return;
        }

        for ( final File file : files )
        {
            copySingle( file, targetDir );
        }
    }

    private String normalizePath( final String path )
    {
        return Joiner.on( '/' ).join( Splitter.on( '/' ).omitEmptyStrings().trimResults().split( path ) ).trim();
    }
}
