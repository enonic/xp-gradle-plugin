package com.enonic.gradle.xp.app;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;

import javax.inject.Inject;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileSystemOperations;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;

public class DeployTask
    extends DefaultTask
{
    private final FileSystemOperations fileSystemOperations;

    private final Property<File> homeDir;

    private final RegularFileProperty from;

    @Inject
    public DeployTask( final FileSystemOperations fileSystemOperations, final ObjectFactory objects )
    {
        this.fileSystemOperations = fileSystemOperations;
        this.homeDir = objects.property( File.class );
        this.from = objects.fileProperty();
        setGroup( "Application" );
        setDescription( "Deploy application to XP_HOME directory." );
        dependsOn( "jar" );
        getOutputs().upToDateWhen( task -> false );
    }

    @Internal
    public RegularFileProperty getFrom()
    {
        return from;
    }

    public void setHomeDir( final Provider<File> dir )
    {
        this.homeDir.set( dir );
    }

    @TaskAction
    public void run()
    {
        final File source = getFrom().get().getAsFile();
        final File deployDir = new File( homeDir.get(), "deploy" );
        final File destination = new File( deployDir, source.getName() );

        if ( isIdentical( source, destination ) )
        {
            getLogger().lifecycle( "Skipping deploy of '{}', already up-to-date", source.getName() );
            return;
        }

        getLogger().lifecycle( "Deploying '{}' to '{}'", source.getName(), deployDir );
        fileSystemOperations.copy( spec -> {
            spec.from( source );
            spec.into( deployDir );
        } );
    }

    private static boolean isIdentical( final File source, final File destination )
    {
        if ( !destination.exists() )
        {
            return false;
        }
        if ( source.length() != destination.length() )
        {
            return false;
        }
        try
        {
            return Files.mismatch( source.toPath(), destination.toPath() ) == -1;
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }
}