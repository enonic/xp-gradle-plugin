package com.enonic.gradle.xp.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gradle.api.Project;

import com.google.common.base.Joiner;

public class AppExtension
{
    private final Project project;

    private String name;

    private String displayName;

    private String url;

    private String vendorName;

    private String vendorUrl;

    private String systemVersion;

    private Map<String, String> instructions;

    private List<File> devSourcePaths;
    
    private List<String> rawDevSourcePaths;

    private boolean systemApp;

    private Set<String> capabilities;

    public AppExtension( final Project project )
    {
        this.project = project;
        this.instructions = new HashMap<>();

        this.devSourcePaths = new ArrayList<>();
        addDevSourcePath( this.project.getProjectDir(), "src", "main", "resources" );
        addDevSourcePath( this.project.getBuildDir(), "resources", "main" );

        this.rawDevSourcePaths = new ArrayList<>();

        this.systemApp = false;
        this.capabilities = new HashSet<>();
    }

    public String getName()
    {
        if ( this.name == null )
        {
            return composeDefaultName();
        }
        else
        {
            return this.name;
        }
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public String getDisplayName()
    {
        return this.displayName != null ? this.displayName : getName();
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public String getUrl()
    {
        return this.url;
    }

    public void setUrl( final String url )
    {
        this.url = url;
    }

    public String getVendorName()
    {
        return this.vendorName;
    }

    public void setVendorName( final String vendorName )
    {
        this.vendorName = vendorName;
    }

    public String getVendorUrl()
    {
        return this.vendorUrl;
    }

    public void setVendorUrl( final String vendorUrl )
    {
        this.vendorUrl = vendorUrl;
    }

    public String getSystemVersion()
    {
        return this.systemVersion;
    }

    public void setSystemVersion( final String systemVersion )
    {
        this.systemVersion = systemVersion;
    }

    public List<File> getDevSourcePaths()
    {
        return this.devSourcePaths;
    }

    public void setDevSourcePaths( final List<File> devSourcePaths )
    {
        this.devSourcePaths = devSourcePaths;
    }

    public List<String> getRawDevSourcePaths()
    {
        return this.rawDevSourcePaths;
    }

    public void setRawDevSourcePaths( final List<String> rawDevSourcePaths )
    {
        this.rawDevSourcePaths = rawDevSourcePaths;
    }

    public Map<String, String> getInstructions()
    {
        return this.instructions;
    }

    public void instruction( final String name, final String value )
    {
        this.instructions.merge( name, value, ( a, b ) -> a + "," + b );
    }

    public Set<String> getCapabilities()
    {
        return this.capabilities;
    }

    public void setCapabilities( final String... values )
    {
        this.capabilities.addAll( Arrays.asList( values ) );
    }

    private String composeDefaultName()
    {
        if ( this.project.getGroup().equals( "" ) )
        {
            return this.project.getName();
        }

        return this.project.getGroup().toString() + "." + this.project.getName();
    }

    private void addDevSourcePath( final File root, final String... paths )
    {
        final File file = new File( root, Joiner.on( File.separatorChar ).join( paths ) );
        this.devSourcePaths.add( file );
    }

    public boolean isSystemApp()
    {
        return this.systemApp;
    }

    public void setSystemApp( final boolean systemApp )
    {
        this.systemApp = systemApp;
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
