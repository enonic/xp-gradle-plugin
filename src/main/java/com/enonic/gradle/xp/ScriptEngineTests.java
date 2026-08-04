package com.enonic.gradle.xp;

import java.util.List;
import java.util.Locale;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.junit.JUnitOptions;
import org.gradle.api.tasks.testing.junitplatform.JUnitPlatformOptions;
import org.gradle.api.tasks.testing.testng.TestNGOptions;
import org.gradle.language.base.plugins.LifecycleBasePlugin;

/**
 * Gives a project one test run per script engine it has to support, so no build has to write that
 * matrix itself.
 * <p>
 * Which engines those are is a property of the project, and only the build knows it: a library runs
 * inside whatever application requires it, so it has to pass on all of them, while an application
 * runs on exactly the one engine it declares. The declared engine lives in {@code app.scriptEngine}
 * and reaches production as the {@code X-Script-Engine} bundle header — the same value decides the
 * tests, so the two can never drift.
 */
final class ScriptEngineTests
{
    /**
     * The system property the XP test harness reads to pick an engine.
     */
    private static final String SCRIPT_ENGINE_PROPERTY = "xp.script-engine";

    private ScriptEngineTests()
    {
    }

    static void configure( final Project project )
    {
        project.getPlugins().withType( JavaPlugin.class, javaPlugin -> project.afterEvaluate( ScriptEngineTests::apply ) );
    }

    private static void apply( final Project project )
    {
        final List<String> engines = XpExtension.get( project ).getScriptEngines().get();
        if ( engines.isEmpty() )
        {
            // nothing declared and nothing to default to: leave `test` untouched so it follows the
            // engine the XP version being built against defaults to
            return;
        }

        final TaskProvider<Test> test = project.getTasks().named( JavaPlugin.TEST_TASK_NAME, Test.class );

        // Name the engine on `test` rather than letting it inherit the platform default. If it
        // inherited, the day XP changes its default both this task and the extra ones below would
        // run the new default and coverage of the other engine would vanish without a failure.
        test.configure( task -> task.systemProperty( SCRIPT_ENGINE_PROPERTY, engines.get( 0 ) ) );

        for ( final String engine : engines.subList( 1, engines.size() ) )
        {
            registerEngineTest( project, test, engine );
        }
    }

    private static void registerEngineTest( final Project project, final TaskProvider<Test> test, final String engine )
    {
        final SourceSet testSourceSet = project.getExtensions()
            .getByType( JavaPluginExtension.class )
            .getSourceSets()
            .getByName( SourceSet.TEST_SOURCE_SET_NAME );

        final TaskProvider<Test> engineTest = project.getTasks().register( taskName( engine ), Test.class, task -> {
            task.setGroup( LifecycleBasePlugin.VERIFICATION_GROUP );
            task.setDescription( "Runs the tests with the " + engine + " script engine." );
            task.setTestClassesDirs( testSourceSet.getOutput().getClassesDirs() );
            task.setClasspath( testSourceSet.getRuntimeClasspath() );
            task.systemProperty( SCRIPT_ENGINE_PROPERTY, engine );
            useSameFrameworkAs( task, test.get() );

            // the same classes on the same output directories: two engines at once would have them
            // writing over each other's results
            task.shouldRunAfter( test );
        } );

        project.getTasks().named( LifecycleBasePlugin.CHECK_TASK_NAME ).configure( check -> check.dependsOn( engineTest ) );
    }

    /**
     * A registered {@link Test} task does not inherit the test framework the build selected for
     * {@code test}, and guessing wrong means it silently finds no tests at all.
     */
    private static void useSameFrameworkAs( final Test task, final Test test )
    {
        final Object options = test.getOptions();
        if ( options instanceof JUnitPlatformOptions )
        {
            task.useJUnitPlatform();
        }
        else if ( options instanceof JUnitOptions )
        {
            task.useJUnit();
        }
        else if ( options instanceof TestNGOptions )
        {
            task.useTestNG();
        }
    }

    private static String taskName( final String engine )
    {
        return "test" + engine.substring( 0, 1 ).toUpperCase( Locale.ROOT ) + engine.substring( 1 );
    }
}
