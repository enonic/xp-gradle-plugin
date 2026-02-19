package com.enonic.gradle.xp.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppPluginTest
{
    @Test
    void xp7IsNotSupported()
    {
        XpVersion xpVersion = XpVersion.parse( "7.13.0" );
        assertThrows( IllegalStateException.class, () -> AppPlugin.ensureCorrectJavaCompilerVersion( null, xpVersion ) );
    }

    @Test
    void xp8IsSupported()
    {
        XpVersion xpVersion = XpVersion.parse( "8.0.0" );
        assertDoesNotThrow( () -> AppPlugin.ensureCorrectJavaCompilerVersion( null, xpVersion ) );
    }
}
