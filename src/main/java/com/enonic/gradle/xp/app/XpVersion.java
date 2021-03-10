package com.enonic.gradle.xp.app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XpVersion
{
    private static final Pattern VERSION_REGEX = Pattern.compile( "(?<major>0|[1-9]\\d*)\\.(?<minor>0|[1-9]\\d*)\\.(?<patch>0|[1-9]\\d*)" +
                                                                      "(?:-(?<prerelease>(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?" +
                                                                      "(?:\\+(?<buildmetadata>[0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?" );

    public final String version;

    public final boolean valid;

    public final long major;

    public final long minor;

    public final long patch;

    public XpVersion( final String version )
    {
        this.version = version;
        final Matcher matcher = VERSION_REGEX.matcher( version );
        if ( matcher.matches() )
        {
            major = Long.parseLong( matcher.group( "major" ) );
            minor = Long.parseLong( matcher.group( "minor" ) );
            patch = Long.parseLong( matcher.group( "patch" ) );
            valid = true;
        }
        else
        {
            major = 0;
            minor = 0;
            patch = 0;
            valid = false;
        }
    }

    public static XpVersion parse( final String version )
    {
        return new XpVersion( version );
    }
}
