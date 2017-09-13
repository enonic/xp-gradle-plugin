package com.enonic.gradle.xp.app;

import java.util.Map;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

import com.google.common.collect.Maps;

final class ExcludeRuleConfigurator
    implements Action<Project>
{
    private final Configuration config;

    ExcludeRuleConfigurator( final Configuration config )
    {
        this.config = config;
    }

    @Override
    public void execute( final Project project )
    {
        addExclusionRules();
    }

    private void addExclusionRules()
    {
        addExclude( "org.slf4j", null );
        addExclude( "org.osgi", null );
        addExclude( "com.enonic.xp", "core-api" );
        addExclude( "com.enonic.xp", "script-api" );
        addExclude( "com.enonic.xp", "web-api" );
        addExclude( "com.enonic.xp", "admin-api" );
        addExclude( "com.enonic.xp", "portal-api" );
        addExclude( "com.google.guava", "guava" );
    }

    private void addExclude( final String group, final String module )
    {
        final Map<String, String> map = Maps.newHashMap();
        if ( group != null )
        {
            map.put( "group", group );
        }

        if ( module != null )
        {
            map.put( "module", module );
        }

        this.config.exclude( map );
    }
}
