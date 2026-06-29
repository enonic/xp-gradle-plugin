package com.enonic.gradle.xp;

import org.gradle.api.provider.Property;

public interface XpSettingsExtension
{
    Property<String> getVersion();
}
