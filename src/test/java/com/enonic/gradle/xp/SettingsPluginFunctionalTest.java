package com.enonic.gradle.xp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SettingsPluginFunctionalTest
{
    @TempDir
    File projectDir;

    private static final String BUILD_GRADLE =
        "plugins { id 'com.enonic.xp.base' }\n" +
        "tasks.register('printXpVersion') {\n" +
        "    doLast { println 'XPVER=' + project.extensions.getByType( com.enonic.gradle.xp.XpExtension ).version.get() }\n" +
        "}\n";

    private void writeFile( final String name, final String content )
        throws IOException
    {
        Files.writeString( new File( projectDir, name ).toPath(), content );
    }

    private BuildResult run()
    {
        return GradleRunner.create()
            .withProjectDir( projectDir )
            .withPluginClasspath()
            .withArguments( "printXpVersion", "-q" )
            .build();
    }

    @Test
    void settingsExtensionVersionIsUsed()
        throws IOException
    {
        writeFile( "settings.gradle", "plugins { id 'com.enonic.xp.settings' }\n" + "xp { version = '8.5.0' }\n" );
        writeFile( "build.gradle", BUILD_GRADLE );

        assertTrue( run().getOutput().contains( "XPVER=8.5.0" ) );
    }

    @Test
    void gradlePropertyUsedWhenNoExtension()
        throws IOException
    {
        writeFile( "settings.gradle", "plugins { id 'com.enonic.xp.settings' }\n" );
        writeFile( "build.gradle", BUILD_GRADLE );
        writeFile( "gradle.properties", "xpVersion=8.4.0\n" );

        assertTrue( run().getOutput().contains( "XPVER=8.4.0" ) );
    }

    @Test
    void defaultVersionWhenNothingConfigured()
        throws IOException
    {
        writeFile( "settings.gradle", "plugins { id 'com.enonic.xp.settings' }\n" );
        writeFile( "build.gradle", BUILD_GRADLE );

        assertTrue( run().getOutput().contains( "XPVER=8.0.2" ) );
    }
}
