package com.enonic.gradle.xp;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XpExtensionTest
{
    @Test
    void versionDefaultsTo802WhenNoCatalogAndNoProperty()
    {
        final Project project = ProjectBuilder.builder().build();
        final XpExtension ext = XpExtension.create( project );

        assertEquals( "8.0.2", ext.getVersion().get() );
    }

    @Test
    void explicitVersionOverridesConvention()
    {
        final Project project = ProjectBuilder.builder().build();
        final XpExtension ext = XpExtension.create( project );
        ext.setVersion( "8.3.0" );

        assertEquals( "8.3.0", ext.getVersion().get() );
    }
}
