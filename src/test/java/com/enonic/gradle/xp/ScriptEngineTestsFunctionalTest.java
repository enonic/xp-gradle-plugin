package com.enonic.gradle.xp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The engine matrix is resolved after the build script has run, so these go through a real build
 * rather than a synthetic project.
 */
class ScriptEngineTestsFunctionalTest
{
    @TempDir
    File projectDir;

    /**
     * The plugin's toolchain convention is the current XP baseline, which the machine running these
     * tests need not have installed. Pinning the probe build to the running JVM keeps them from
     * depending on that.
     */
    private static final String TOOLCHAIN = "java { toolchain { languageVersion = JavaLanguageVersion.of(" +
        Runtime.version().feature() + ") } }\n";

    /**
     * Reports what the wiring produced: which extra test tasks exist, what engine each task got,
     * and whether {@code check} picked the extra ones up.
     */
    private static final String REPORT_TASK = "tasks.register('reportEngines') {\n" +
        "    doLast {\n" +
        "        tasks.withType(Test).sort { it.name }.each { t ->\n" +
        "            println 'TASK=' + t.name + ' engine=' + t.systemProperties['xp.script-engine']\n" +
        "        }\n" +
        "        println 'CHECK=' + tasks.check.taskDependencies.getDependencies(tasks.check)*.name.sort().join(',')\n" +
        "    }\n" +
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
            .withArguments( "reportEngines", "-q" )
            .build();
    }

    private String build( final String plugins, final String config )
        throws IOException
    {
        writeFile( "settings.gradle", "plugins { id 'com.enonic.xp.settings' }\nrootProject.name = 'probe'\n" );
        writeFile( "build.gradle", "plugins {\n" + plugins + "}\n" + TOOLCHAIN + config + REPORT_TASK );
        return run().getOutput();
    }

    @Test
    void aLibraryIsTestedOnNashornByDefault()
        throws IOException
    {
        final String output = build( "    id 'java'\n    id 'com.enonic.xp.base'\n", "" );

        // only the engine every supported XP version can run: nothing that a version being built
        // against might not provide is asked for unless the project opts in
        assertTrue( output.contains( "TASK=test engine=Nashorn" ), output );
        assertFalse( output.contains( "TASK=testGraalJS" ), output );
    }

    @Test
    void aProjectCanOptIntoAnotherEngine()
        throws IOException
    {
        final String output =
            build( "    id 'java'\n    id 'com.enonic.xp.base'\n", "xp { scriptEngines = ['Nashorn', 'GraalJS'] }\n" );

        // the task name follows the engine name, so GraalJS gives testGraalJS
        assertTrue( output.contains( "TASK=test engine=Nashorn" ), output );
        assertTrue( output.contains( "TASK=testGraalJS engine=GraalJS" ), output );

        // and it has to be reachable from check, or the extra engine would never run
        assertTrue( output.lines().anyMatch( line -> line.startsWith( "CHECK=" ) && line.contains( "testGraalJS" ) ), output );
    }

    @Test
    void anApplicationIsTestedOnTheEngineItDeclares()
        throws IOException
    {
        final String output = build( "    id 'java'\n    id 'com.enonic.xp.app'\n", "app { scriptEngine = 'GraalJS' }\n" );

        // the engine it will actually run on, and only that one
        assertTrue( output.contains( "TASK=test engine=GraalJS" ), output );
        assertFalse( output.contains( "TASK=testNashorn" ), output );
        assertFalse( output.contains( "TASK=testGraalJS" ), output );
    }

    @Test
    void anApplicationWithoutADeclaredEngineIsLeftAlone()
        throws IOException
    {
        final String output = build( "    id 'java'\n    id 'com.enonic.xp.app'\n", "" );

        // no property, so the harness applies the default of the XP version being built against
        assertTrue( output.contains( "TASK=test engine=null" ), output );
        assertFalse( output.contains( "TASK=testGraalJS" ), output );
    }

    @Test
    void engineTasksRunTheSameTestsAsTheTestTask()
        throws IOException
    {
        writeFile( "settings.gradle", "plugins { id 'com.enonic.xp.settings' }\nrootProject.name = 'probe'\n" );
        writeFile( "build.gradle", "plugins {\n    id 'java'\n    id 'com.enonic.xp.base'\n}\n" + TOOLCHAIN +
            "xp { scriptEngines = ['Nashorn', 'GraalJS'] }\n" +
            "tasks.register('compareClasspaths') {\n" +
            "    doLast {\n" +
            "        println 'SAME_DIRS=' + (tasks.testGraalJS.testClassesDirs.files == tasks.test.testClassesDirs.files)\n" +
            "        println 'SAME_CP=' + (tasks.testGraalJS.classpath.files == tasks.test.classpath.files)\n" +
            "    }\n" +
            "}\n" );

        final String output = GradleRunner.create()
            .withProjectDir( projectDir )
            .withPluginClasspath()
            .withArguments( "compareClasspaths", "-q" )
            .build()
            .getOutput();

        assertTrue( output.contains( "SAME_DIRS=true" ), output );
        assertTrue( output.contains( "SAME_CP=true" ), output );
    }
}
