# XP Gradle Plugins

[![Actions Status](https://github.com/enonic/xp-gradle-plugin/workflows/Gradle%20Build/badge.svg)](https://github.com/enonic/xp-gradle-plugin/actions)
[![License](https://img.shields.io/github/license/enonic/xp-gradle-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

Gradle plugins that simplify development of libraries and applications for Enonic XP.

For Enonic XP 7.x, use version 3.6.2.

For Enonic XP 8.x, use version 4.0.0 or later.

This repository provides three plugins:

- **`com.enonic.xp.settings`** — registers the `xplibs` version catalog with Enonic XP dependencies. The XP version is taken from the `xp { version = "…" }` settings block, then the `xpVersion` Gradle property, falling back to a built-in default version.
- **`com.enonic.xp.base`** — base plugin for library and application development (Enonic repositories, the `xp {}` extension, Java toolchain).
- **`com.enonic.xp.app`** — builds Enonic XP application jar files (automatically applies the base plugin).

## Documentation

Usage documentation for all three plugins lives in [`docs/index.adoc`](docs/index.adoc) and is published to the [Enonic Developer Portal](https://developer.enonic.com/).

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
    id("com.enonic.xp.settings") version "4.1.0-SNAPSHOT"
}
```

## Development

To build and publish the plugin to your local maven repository:

```
./gradlew publishToMavenLocal
```

## Releasing

Releases are cut from a version branch, not from `master` (which tracks the next, unreleased version). Check out the branch that matches the version you are releasing:

- `4.x` — for Enonic XP 8.x releases (`4.0.0`, `4.0.1`, …)
- `3.x` — for Enonic XP 7.x releases (`3.6.x`)

On that branch, change the version in `build.gradle.kts` (for instance `version = "4.0.0"`),
tag the commit with the version number (for instance `git tag v4.0.0`) and push to GitHub (`git push --follow-tags`).

After the release is done, update the version in `build.gradle.kts` to the next snapshot version (for instance `version = "4.0.1-SNAPSHOT"`).
