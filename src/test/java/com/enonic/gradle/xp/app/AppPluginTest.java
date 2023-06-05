package com.enonic.gradle.xp.app;

import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.compile.JavaCompile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppPluginTest
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Project project;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    JavaCompile javaCompile;

    @Mock
    Property<Integer> release;

    @BeforeEach
    void setup()
    {
        when( project.getTasks().findByName( "compileJava" ) ).thenReturn( javaCompile );
        when( javaCompile.getOptions().getRelease() ).thenReturn( release );
    }

    @Test
    void version712PresetJava17()
    {
        when( release.isPresent() ).thenReturn( true );
        when( release.get() ).thenReturn( 17 );
        XpVersion xpVersion = XpVersion.parse( "7.12.0" );
        assertThrows( IllegalStateException.class, () -> AppPlugin.ensureCorrectJavaCompilerVersion( project, xpVersion ) );
    }

    @Test
    void versionBelow712PresetJava17()
    {
        when( release.isPresent() ).thenReturn( true );
        when( release.get() ).thenReturn( 17 );
        XpVersion xpVersion = XpVersion.parse( "7.11.0" );
        assertThrows( IllegalStateException.class, () -> AppPlugin.ensureCorrectJavaCompilerVersion( project, xpVersion ) );
    }

    @Test
    void version712SetJava11()
    {
        XpVersion xpVersion = XpVersion.parse( "7.12.2" );
        when( release.isPresent() ).thenReturn( false );
        AppPlugin.ensureCorrectJavaCompilerVersion( project, xpVersion );
        verify( release ).set( 11 );
    }

    @Test
    void versionBelow712SetJava11()
    {
        XpVersion xpVersion = XpVersion.parse( "7.0.0" );
        when( release.isPresent() ).thenReturn( false );
        AppPlugin.ensureCorrectJavaCompilerVersion( project, xpVersion );
        verify( release ).set( 11 );
    }

    @Test
    void version713DoNothing()
    {
        // For now, we don't want to enforce Java 17 for XP 7.13
        XpVersion xpVersion = XpVersion.parse( "7.13.0-SNAPSHOT" );
        AppPlugin.ensureCorrectJavaCompilerVersion( project, xpVersion );
        verifyNoInteractions( release );
    }
}
