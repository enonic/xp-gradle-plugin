package com.enonic.gradle.xp.doc;

public class S3Settings
{
    private final static String DEFAULT_ENDPOINT = "https://s3.amazonaws.com";

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucketName;

    public S3Settings()
    {
        this.endpoint = DEFAULT_ENDPOINT;
    }

    public String getEndpoint()
    {
        return this.endpoint;
    }

    public void setEndpoint( final String endpoint )
    {
        this.endpoint = endpoint;
    }

    public String getAccessKey()
    {
        return this.accessKey;
    }

    public void setAccessKey( final String accessKey )
    {
        this.accessKey = accessKey;
    }

    public String getSecretKey()
    {
        return this.secretKey;
    }

    public void setSecretKey( final String secretKey )
    {
        this.secretKey = secretKey;
    }

    public String getBucketName()
    {
        return this.bucketName;
    }

    public void setBucketName( final String bucketName )
    {
        this.bucketName = bucketName;
    }
}
