package com.enonic.gradle.xp.doc;

import java.io.File;

import org.gradle.api.GradleException;
import org.gradle.api.Project;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

final class UploadTask
{
    private final Project project;

    private final DocExtension ext;

    private File zipFile;

    public UploadTask( final Project project, final DocExtension ext )
    {
        this.project = project;
        this.ext = ext;
    }

    public void setZipFile( final File zipFile )
    {
        this.zipFile = zipFile;
    }

    public void upload()
        throws Exception
    {
        final RequestBody zip = RequestBody.create( MediaType.parse( "application/zip" ), this.zipFile );

        final MultipartBody body = new MultipartBody.Builder().
            addFormDataPart( "file", "file.zip", zip ).
            addFormDataPart( "id", "lib-cache" ).
            addFormDataPart( "version", "1.0.0" ).
            setType( MediaType.parse( "multipart/form-data" ) ).
            build();

        final Request request = new Request.Builder().
            url( this.ext.getUpload().getEndpoint() ).
            header( "Authorization", Credentials.basic( "su", "password") ).
            post( body ).
            // addHeader( "X-DocId", "lib-cache" ).
            // addHeader( "X-DocVersion", "x.x.x" ).
                build();

        final OkHttpClient client = new OkHttpClient.Builder().
            build();

        final Response response = client.newCall( request ).execute();

        if ( response.isSuccessful() )
        {
            return;
        }

        final int code = response.code();
        final ResponseBody resBody = response.body();
        final String text = resBody != null ? resBody.string() : "Empty message";

        throw new GradleException( "Failed to upload [code = " + code + ", message = " + text + "]" );
    }
}
