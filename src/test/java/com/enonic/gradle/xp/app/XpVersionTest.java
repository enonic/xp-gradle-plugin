package com.enonic.gradle.xp.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XpVersionTest
{
    @Test
    void parse_version()
    {
        final XpVersion parsed = XpVersion.parse( "7.14.3" );
        assertTrue( parsed.valid );
        assertEquals( 7, parsed.major );
        assertEquals( 14, parsed.minor );
        assertEquals( 3, parsed.patch );
        assertEquals( "[7.14,8)", parsed.range );
    }

    @Test
    void parse_range()
    {
        final XpVersion parsed = XpVersion.parse( "[7,8)" );
        assertTrue( parsed.valid );
        assertEquals( 7, parsed.major );
        assertEquals( 0, parsed.minor );
        assertEquals( 0, parsed.patch );
        assertEquals( "[7,8)", parsed.range );
    }

    @Test
    void parse_range_2()
    {
        final XpVersion parsed = XpVersion.parse( "[7.14.3,8.1.5)" );
        assertTrue( parsed.valid );
        assertEquals( 7, parsed.major );
        assertEquals( 14, parsed.minor );
        assertEquals( 3, parsed.patch );
        assertEquals( "[7.14.3,8.1.5)", parsed.range );
    }
}
