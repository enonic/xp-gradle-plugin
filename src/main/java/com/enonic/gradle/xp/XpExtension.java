package com.enonic.gradle.xp;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.artifacts.VersionCatalogsExtension;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

public class XpExtension
{
    private final Project project;

    private final Property<String> version;

    private final DirectoryProperty homeDir;

    public XpExtension( final Project project )
    {
        this.project = project;
        final ObjectFactory objects = project.getObjects();
        this.version = objects.property( String.class );
        this.version.convention( project.provider(
            () -> XpVersionResolver.resolveVersion( xplibsCatalogVersion( project ),
                                                    project.getProviders().gradleProperty( "xpVersion" ).getOrNull() ) ) );

        this.homeDir = objects.directoryProperty();
        this.homeDir.convention( project.getProviders()
                                     .gradleProperty( "xpHome" )
                                     .orElse( project.getProviders().systemProperty( "xp.home" ) )
                                     .orElse( project.getProviders().environmentVariable( "XP_HOME" ) )
                                     .map( path -> objects.directoryProperty().fileValue( new File( path ) ).get() )
                                     .orElse( project.getLayout().getBuildDirectory().dir( "xp/home" ) ) );
    }

    private static String xplibsCatalogVersion( final Project project )
    {
        final VersionCatalogsExtension catalogs = project.getExtensions().findByType( VersionCatalogsExtension.class );
        if ( catalogs == null )
        {
            return null;
        }
        return catalogs.find( "xplibs" )
            .flatMap( catalog -> catalog.findVersion( "xp" ) )
            .map( version -> version.getRequiredVersion() )
            .orElse( null );
    }

    public Property<String> getVersion()
    {
        return this.version;
    }

    public void setVersion( final String version )
    {
        this.version.set( version );
    }

    public DirectoryProperty getHomeDir()
    {
        return this.homeDir;
    }

    public void setHomeDir( final File dir )
    {
        this.homeDir.set( dir );
    }

    public static XpExtension get( final Project project )
    {
        return project.getExtensions().getByType( XpExtension.class );
    }

    public static XpExtension create( final Project project )
    {
        return project.getExtensions().create( "xp", XpExtension.class, project );
    }

    public MavenArtifactRepository enonicRepo()
    {
        return enonicRepo( "public" );
    }

    public MavenArtifactRepository enonicRepo( final String name )
    {
        return this.project.getRepositories().maven( repo -> repo.setUrl( "https://repo.enonic.com/" + name ) );
    }
}
