package com.enonic.gradle.xp.app;

import java.util.List;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DevTaskTest
{
    @TempDir
    File projectDir;

    @Test
    void commandLineContainsDeployAndContinuousAndDev()
    {
        final Project project = ProjectBuilder.builder().withProjectDir( projectDir ).build();
        final DevTask task = project.getTasks().create( "dev", DevTask.class );
        task.getContinuousTaskName().set( "deploy" );

        final List<String> cmd = task.buildCommandLine();

        assertTrue( cmd.stream().anyMatch( arg -> arg.endsWith( "gradlew" ) || arg.endsWith( "gradlew.bat" ) ) );
        assertTrue( cmd.contains( "deploy" ) );
        assertTrue( cmd.contains( "--continuous" ) );
        assertTrue( cmd.contains( "-Penv=dev" ) );
    }

    @Test
    void xpHomePropertyIsForwarded()
    {
        final Project project = ProjectBuilder.builder().withProjectDir( projectDir ).build();
        final DevTask task = project.getTasks().create( "dev", DevTask.class );
        task.getContinuousTaskName().set( "deploy" );
        task.getXpHome().set( "/opt/xp-home" );

        final List<String> cmd = task.buildCommandLine();

        assertTrue( cmd.contains( "-PxpHome=/opt/xp-home" ) );
    }

    @Test
    void xpHomePropertyNotSetIsNotForwarded()
    {
        final Project project = ProjectBuilder.builder().withProjectDir( projectDir ).build();
        final DevTask task = project.getTasks().create( "dev", DevTask.class );
        task.getContinuousTaskName().set( "deploy" );

        final List<String> cmd = task.buildCommandLine();

        assertTrue( cmd.stream().noneMatch( arg -> arg.startsWith( "-PxpHome=" ) ) );
    }

    @Test
    void multiProjectPathQualification()
    {
        final Project root = ProjectBuilder.builder().withProjectDir( projectDir ).build();
        final File subDir = new File( projectDir, "myapp" );
        subDir.mkdirs();
        final Project sub = ProjectBuilder.builder().withParent( root ).withProjectDir( subDir ).withName( "myapp" ).build();
        final DevTask task = sub.getTasks().create( "dev", DevTask.class );
        task.getContinuousTaskName().set( ":myapp:deploy" );

        final List<String> cmd = task.buildCommandLine();

        assertTrue( cmd.contains( ":myapp:deploy" ) );
    }
}
