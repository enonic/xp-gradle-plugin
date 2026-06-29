package com.enonic.gradle.xp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XpVersionResolverTest
{
    @Test
    void defaultsWhenBothMissing()
    {
        assertEquals( "8.0.2", XpVersionResolver.resolveVersion( null, null ) );
    }

    @Test
    void primaryWinsOverProperty()
    {
        assertEquals( "8.5.0", XpVersionResolver.resolveVersion( "8.5.0", "8.4.0" ) );
    }

    @Test
    void propertyUsedWhenNoPrimary()
    {
        assertEquals( "8.4.0", XpVersionResolver.resolveVersion( null, "8.4.0" ) );
    }

    @Test
    void blankPrimaryFallsThroughToProperty()
    {
        assertEquals( "8.4.0", XpVersionResolver.resolveVersion( "   ", "8.4.0" ) );
    }

    @Test
    void valuesAreTrimmed()
    {
        assertEquals( "8.5.0", XpVersionResolver.resolveVersion( "  8.5.0  ", null ) );
    }
}
