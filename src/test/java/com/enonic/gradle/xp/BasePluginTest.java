package com.enonic.gradle.xp;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BasePluginTest
{
    @Test
    void preservesFileTimestampsOnArchiveTasks()
    {
        final Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply( JavaPlugin.class );

        final Jar jar = (Jar) project.getTasks().getByName( "jar" );
        // Gradle 9 defaults preserveFileTimestamps to false (reproducible builds). Set it
        // explicitly here to prove the plugin actively forces the value back to true.
        jar.setPreserveFileTimestamps( false );

        project.getPlugins().apply( BasePlugin.class );

        assertTrue( jar.isPreserveFileTimestamps(), "BasePlugin should preconfigure preserveFileTimestamps = true on archive tasks" );
    }
}