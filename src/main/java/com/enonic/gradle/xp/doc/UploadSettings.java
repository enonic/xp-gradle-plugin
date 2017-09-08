package com.enonic.gradle.xp.doc;

public class UploadSettings
{
    private final static String DEFAULT_ENDPOINT = "http://localhost:8080/portal/master/dev/_/service/com.enonic.app.docportal/upload";

    private String endpoint;

    private String accessKey;

    public UploadSettings()
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
}
