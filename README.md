# XP Gradle Plugins

[![Actions Status](https://github.com/enonic/xp-gradle-plugin/workflows/Gradle%20Build/badge.svg)](https://github.com/enonic/xp-gradle-plugin/actions)
[![License](https://img.shields.io/github/license/enonic/xp-gradle-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

For Enonic XP 7.x, use version 3.6.2.

For Enonic XP 8.x, use version 4.0.0 or later.

## Plugins

### Settings Plugin (`com.enonic.xp.settings`)

Apply in `settings.gradle.kts`:

```kotlin
plugins {
    id("com.enonic.xp.settings") version "4.0.0"
}
```

Define `xpVersion` in `gradle.properties`

```properties
xpVersion = 8.0.0
```

Creates a `xplib` version catalog with XP dependencies.

Example usage in `build.gradle.kts`:

```kotlin
dependencies {
    // Java API dependencies
    implementation(xplib.api.core)
    implementation(xplib.api.portal)

    // JavaScript Library dependencies
    include(xplib.content)
    include(xplib.portal)
    include(xplib.context)
}
```


### Base Plugin (`com.enonic.xp.base`)

Base plugin for Enonic XP library and application development.

```kotlin
plugins {
    id("com.enonic.xp.base") version "4.0.0"
}
```

When the Java plugin is applied, the base plugin:

- Sets the Java toolchain to version 25 (as a convention default)

### App Plugin (`com.enonic.xp.app`)

Plugin for Enonic XP application development. Automatically applies the base plugin.

```kotlin
plugins {
    id("com.enonic.xp.app") version "4.0.0"
}
```

#### App Defaults from `gradle.properties`

The `app {}` extension reads defaults from Gradle project properties. These can be set in `gradle.properties`:

```properties
xpVersion = 8.0.0
appName = com.example.myapp
appDisplayName = My Application
vendorName = Example Inc
vendorUrl = https://example.com
```

| Property | `gradle.properties` key | Fallback |
|---|---|---|
| `systemVersion` | `xpVersion` | _(required)_ |
| `name` | `appName` | `${group}.${project.name}` |
| `displayName` | `appDisplayName` | value of `name` |
| `vendorName` | `vendorName` | _(none)_ |
| `vendorUrl` | `vendorUrl` | _(none)_ |

Any property explicitly set in the `app {}` block takes precedence:

```kotlin
app {
    name = "com.example.override"
    displayName = "Override Display Name"
}
```

#### Java Toolchain

The base plugin sets the Java toolchain language version to 25 as a convention default.

## Development Setup

To use the locally built plugin with Gradle 9, configure `pluginManagement` in `settings.gradle.kts`:

```kotlin
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

plugins {
    id("com.enonic.xp.settings") version "4.0.0-SNAPSHOT"
}
```

Then in `build.gradle.kts`:

```kotlin
plugins {
    id("com.enonic.xp.app") version "4.0.0-SNAPSHOT"
}
```

## Development

To build and publish the plugin to your local maven repository:

```
./gradlew publishToMavenLocal
```

## Releasing

To release a new version of the plugin, change the version in `build.gradle.kts` (for instance `version = "4.0.0"`),
tag the commit with the version number (for instance `git tag v4.0.0`) and push to GitHub (`git push --follow-tags`).

After the release is done, update the version in `build.gradle.kts` to the next snapshot version (for instance `version = "4.0.1-SNAPSHOT"`).
