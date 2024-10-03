# XP Gradle Plugins

[![Actions Status](https://github.com/enonic/xp-gradle-plugin/workflows/Gradle%20Build/badge.svg)](https://github.com/enonic/xp-gradle-plugin/actions)
[![License](https://img.shields.io/github/license/enonic/xp-gradle-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

[Documentation](docs/index.adoc)

For Enonic XP 6.x, use version 1.2.0.

## Development

To build and publish the plugin to your local maven repository, run `./gradlew publishToMavenLocal`

To use the locally built plugin in your project comment out the plugin line in your `build.gradle` file:
```
id 'com.enonic.xp.app' version '3.4.0'
```

and add the following to your `build.gradle` file:
```groovy
buildscript {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven {
            url 'https://repo.enonic.com/public'
        }
    }
    dependencies {
        classpath "com.enonic.gradle:xp-gradle-plugin:3.4.0-SNAPSHOT"
    }
}

apply plugin: 'com.enonic.xp.app'
```

## Releasing

To release a new version of the plugin, change the version in `build.gradle.kts` (for instance `version = "3.3.0"`, 
tag the commit with the version number (for instance `git tag v3.3.0`) and push to GitHub (`git push --follow-tags`).

After the release is done, update the version in `build.gradle.kts` to the next snapshot version (for instance `version = "3.3.1-SNAPSHOT"`).
