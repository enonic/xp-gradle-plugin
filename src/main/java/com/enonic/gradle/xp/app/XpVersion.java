package com.enonic.gradle.xp.app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XpVersion
{
    private static final Pattern VERSION_REGEX = Pattern.compile( "(?<major>0|[1-9]\\d*)\\.(?<minor>0|[1-9]\\d*)\\.(?<patch>0|[1-9]\\d*)" +
                                                                      "(?:-(?<prerelease>(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?" +
                                                                      "(?:\\+(?<buildmetadata>[0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?" );

    private static final Pattern VERSION_RANGE_REGEX = Pattern.compile(
        "\\[(?<major>0|[1-9]\\d*)(?:\\.(?<minor>0|[1-9]\\d*))?(?:\\.(?<patch>0|[1-9]\\d*))?,(?:0|[1-9]\\d*)(?:\\.(?:0|[1-9])\\d*)?(?:\\.(?:0|[1-9])\\d*)?\\)" );


    public final String version;

    public final boolean valid;

    public final long major;

    public final long minor;

    public final long patch;

    public final String range;

    public XpVersion( final String version )
    {
        this.version = version;
        final Matcher versionMatcher = VERSION_REGEX.matcher( version );
        if ( versionMatcher.matches() )
        {
            major = Long.parseLong( versionMatcher.group( "major" ) );
            minor = Long.parseLong( versionMatcher.group( "minor" ) );
            patch = Long.parseLong( versionMatcher.group( "patch" ) );
            range = "[" + major + "." + minor + "," + Math.addExact( major, 1 ) + ")";
            valid = true;
            return;
        }

        final Matcher rangeMatcher = VERSION_RANGE_REGEX.matcher( version );
        if ( rangeMatcher.matches() )
        {
            major = Long.parseLong( rangeMatcher.group( "major" ) );
            minor = rangeMatcher.group( "minor" ) != null ? Long.parseLong( rangeMatcher.group( "minor" ) ) : 0;
            patch = rangeMatcher.group( "patch" ) != null ? Long.parseLong( rangeMatcher.group( "patch" ) ) : 0;
            range = version;
            valid = true;
        }
        else
        {
            major = 0;
            minor = 0;
            patch = 0;
            range = "[0.0.0,0)";
            valid = false;
        }
    }

    public static XpVersion parse( final String version )
    {
        return new XpVersion( version );
    }
}
