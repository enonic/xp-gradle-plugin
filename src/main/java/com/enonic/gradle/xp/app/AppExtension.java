package com.enonic.gradle.xp.app;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;

public class AppExtension
{
    private final Project project;

    private final Property<String> name;

    private final Property<String> displayName;

    private final Property<String> url;

    private final Property<String> vendorName;

    private final Property<String> vendorUrl;

    private final Property<String> systemVersion;

    private final Property<String> scriptEngine;

    private final Property<Boolean> systemApp;

    private final Property<Boolean> keepArchiveFileName;

    private final Property<Boolean> allowDevSourcePathsPublishing;

    private final Property<Boolean> createDefaultDevTask;

    private final Property<String> continuousTaskName;

    private final Map<String, String> instructions;

    private final ListProperty<File> devSourcePaths;

    private final ListProperty<String> rawDevSourcePaths;

    private final SetProperty<String> capabilities;

    public AppExtension( final Project project )
    {
        this.project = project;
        final ObjectFactory objects = project.getObjects();

        this.name = objects.property( String.class );
        this.name.convention( project.getProviders().gradleProperty( "appName" ).orElse( project.provider( this::composeDefaultName ) ) );

        this.displayName = objects.property( String.class );
        this.displayName.convention( project.getProviders().gradleProperty( "appDisplayName" ).orElse( this.name ) );

        this.url = objects.property( String.class );

        this.vendorName = objects.property( String.class );
        this.vendorName.convention( project.getProviders().gradleProperty( "vendorName" ) );

        this.vendorUrl = objects.property( String.class );
        this.vendorUrl.convention( project.getProviders().gradleProperty( "vendorUrl" ) );

        this.systemVersion = objects.property( String.class );

        this.scriptEngine = objects.property( String.class );

        this.systemApp = objects.property( Boolean.class );
        this.systemApp.convention( false );

        this.keepArchiveFileName = objects.property( Boolean.class );
        this.keepArchiveFileName.convention( false );

        this.allowDevSourcePathsPublishing = objects.property( Boolean.class );
        this.allowDevSourcePathsPublishing.convention( false );

        this.createDefaultDevTask = objects.property( Boolean.class );
        this.createDefaultDevTask.convention( true );

        this.continuousTaskName = objects.property( String.class );
        this.continuousTaskName.convention( "deploy" );

        this.instructions = new HashMap<>();

        this.devSourcePaths = objects.listProperty( File.class );
        this.devSourcePaths.convention( project.provider(
            () -> List.of( project.getLayout().getProjectDirectory().dir( "src/main/resources" ).getAsFile(),
                           project.getLayout().getBuildDirectory().get().dir( "resources/main" ).getAsFile() ) ) );

        this.rawDevSourcePaths = objects.listProperty( String.class );
        this.rawDevSourcePaths.convention( List.of() );

        this.capabilities = objects.setProperty( String.class );
        this.capabilities.convention( Set.of() );
    }

    public Property<String> getName()
    {
        return this.name;
    }

    public void setName( final String name )
    {
        this.name.set( name );
    }

    public Property<String> getDisplayName()
    {
        return this.displayName;
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName.set( displayName );
    }

    public Property<String> getUrl()
    {
        return this.url;
    }

    public void setUrl( final String url )
    {
        this.url.set( url );
    }

    public Property<String> getVendorName()
    {
        return this.vendorName;
    }

    public void setVendorName( final String vendorName )
    {
        this.vendorName.set( vendorName );
    }

    public Property<String> getVendorUrl()
    {
        return this.vendorUrl;
    }

    public void setVendorUrl( final String vendorUrl )
    {
        this.vendorUrl.set( vendorUrl );
    }

    public Property<String> getSystemVersion()
    {
        return this.systemVersion;
    }

    public void setSystemVersion( final String systemVersion )
    {
        this.systemVersion.set( systemVersion );
    }

    public Property<String> getScriptEngine()
    {
        return this.scriptEngine;
    }

    public void setScriptEngine( final String scriptEngine )
    {
        this.scriptEngine.set( scriptEngine );
    }

    public Property<Boolean> getSystemApp()
    {
        return this.systemApp;
    }

    public void setSystemApp( final boolean systemApp )
    {
        this.systemApp.set( systemApp );
    }

    public Property<Boolean> getKeepArchiveFileName()
    {
        return this.keepArchiveFileName;
    }

    public void setKeepArchiveFileName( final boolean keepArchiveFileName )
    {
        this.keepArchiveFileName.set( keepArchiveFileName );
    }

    public Property<Boolean> getAllowDevSourcePathsPublishing()
    {
        return this.allowDevSourcePathsPublishing;
    }

    public void setAllowDevSourcePathsPublishing( final boolean allowDevSourcePathsPublishing )
    {
        this.allowDevSourcePathsPublishing.set( allowDevSourcePathsPublishing );
    }

    public Property<Boolean> getCreateDefaultDevTask()
    {
        return this.createDefaultDevTask;
    }

    public void setCreateDefaultDevTask( final boolean createDefaultDevTask )
    {
        this.createDefaultDevTask.set( createDefaultDevTask );
    }

    public Property<String> getContinuousTaskName()
    {
        return this.continuousTaskName;
    }

    public void setContinuousTaskName( final String continuousTaskName )
    {
        this.continuousTaskName.set( continuousTaskName );
    }

    public ListProperty<File> getDevSourcePaths()
    {
        return this.devSourcePaths;
    }

    public ListProperty<String> getRawDevSourcePaths()
    {
        return this.rawDevSourcePaths;
    }

    public Map<String, String> getInstructions()
    {
        return this.instructions;
    }

    public void instruction( final String name, final String value )
    {
        this.instructions.merge( name, value, ( a, b ) -> a + "," + b );
    }

    public SetProperty<String> getCapabilities()
    {
        return this.capabilities;
    }

    private String composeDefaultName()
    {
        if ( this.project.getGroup().equals( "" ) )
        {
            return this.project.getName();
        }

        return this.project.getGroup() + "." + this.project.getName();
    }

    public static AppExtension get( final Project project )
    {
        return project.getExtensions().getByType( AppExtension.class );
    }

    public static AppExtension create( final Project project )
    {
        return project.getExtensions().create( "app", AppExtension.class, project );
    }
}
