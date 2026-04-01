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

#### `xp {}` Extension

The base plugin creates the `xp {}` extension:

```kotlin
xp {
    version = "8.0.0"
    homeDir = file("/path/to/xp/home")
}
```

| Property | Default | Description |
|---|---|---|
| `version` | `xpVersion` from `gradle.properties` | XP version |
| `homeDir` | `xpHome` gradle property, `xp.home` system property, `XP_HOME` env variable, or `${buildDir}/xp/home` | XP home directory |

The extension also provides a helper to add Enonic Maven repositories:

```kotlin
repositories {
    xp.enonicRepo()           // https://repo.enonic.com/public
    xp.enonicRepo("snapshot") // https://repo.enonic.com/snapshot
}
```

### App Plugin (`com.enonic.xp.app`)

Plugin for Enonic XP application development. Automatically applies the base plugin.

```kotlin
plugins {
    id("com.enonic.xp.app") version "4.0.0"
}
```

#### `app {}` Extension

| Property | Default | Description |
|---|---|---|
| `systemVersion` | `xp.version` | XP system version. Supports version ranges in interval notation (e.g. `[8.0,9)`) |
| `name` | `appName` gradle property, or `${group}.${project.name}` | Application name |

```kotlin
app {
    name = "com.example.myapp"
    systemVersion = "[8.0,9)"
}
```

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

## Development

To build and publish the plugin to your local maven repository:

```
./gradlew publishToMavenLocal
```

## Releasing

To release a new version of the plugin, change the version in `build.gradle.kts` (for instance `version = "4.0.0"`),
tag the commit with the version number (for instance `git tag v4.0.0`) and push to GitHub (`git push --follow-tags`).

After the release is done, update the version in `build.gradle.kts` to the next snapshot version (for instance `version = "4.0.1-SNAPSHOT"`).
