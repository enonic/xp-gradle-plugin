package com.enonic.gradle.xp.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.gradle.api.GradleException;
import org.gradle.api.DefaultTask;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.os.OperatingSystem;
import org.gradle.process.ExecOperations;

public class DevTask
    extends DefaultTask
{
    private final ExecOperations execOperations;

    private final File rootDir;

    private final Property<String> continuousTaskName;

    private final Property<String> xpHome;

    private final boolean alreadyContinuous;

    @Inject
    public DevTask( final ExecOperations execOperations, final ObjectFactory objects, final ProviderFactory providers )
    {
        this.execOperations = execOperations;
        this.rootDir = getProject().getRootDir();
        this.continuousTaskName = objects.property( String.class );
        this.xpHome = objects.property( String.class ).convention( providers.gradleProperty( "xpHome" ) );
        this.alreadyContinuous = getProject().getGradle().getStartParameter().isContinuous();
        setGroup( "Application" );
        setDescription( "Run deploy in continuous mode for development." );
    }

    @Input
    public Property<String> getContinuousTaskName()
    {
        return continuousTaskName;
    }

    @Optional
    @Input
    public Property<String> getXpHome()
    {
        return xpHome;
    }

    @TaskAction
    public void run()
    {
        if ( alreadyContinuous )
        {
            throw new GradleException( "The 'dev' task cannot be used when Gradle is already running in continuous mode (--continuous / -t)." );
        }
        execOperations.exec( execSpec -> execSpec.commandLine( buildCommandLine() ) );
    }

    List<String> buildCommandLine()
    {
        final List<String> command = new ArrayList<>();

        if ( OperatingSystem.current().isWindows() )
        {
            command.add( "cmd" );
            command.add( "/c" );
            command.add( new File( rootDir, "gradlew.bat" ).getAbsolutePath() );
        }
        else
        {
            command.add( new File( rootDir, "gradlew" ).getAbsolutePath() );
        }

        command.add( continuousTaskName.get() );
        command.add( "--continuous" );
        command.add( "-Penv=dev" );

        if ( xpHome.isPresent() )
        {
            command.add( "-PxpHome=" + xpHome.get() );
        }

        return command;
    }
}