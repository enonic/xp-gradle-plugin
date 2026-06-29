package com.enonic.gradle.xp;

final class XpVersionResolver
{
    static final String DEFAULT_XP_VERSION = "8.0.2";

    private XpVersionResolver()
    {
    }

    static String resolveVersion( final String primary, final String property )
    {
        if ( isPresent( primary ) )
        {
            return primary.trim();
        }
        if ( isPresent( property ) )
        {
            return property.trim();
        }
        return DEFAULT_XP_VERSION;
    }

    private static boolean isPresent( final String value )
    {
        return value != null && !value.isBlank();
    }
}
