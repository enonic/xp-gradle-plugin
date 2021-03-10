# XP Gradle Plugin

[![Actions Status](https://github.com/enonic/xp-gradle-plugin/workflows/Gradle%20Build/badge.svg)](https://github.com/enonic/xp-gradle-plugin/actions)
[![License](https://img.shields.io/github/license/enonic/xp-gradle-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

This Gradle plugin does the following:

* Build application jar files for Enonic XP (`com.enonic.xp.app`).
* Configures easy steps for deploying artifacts to a repo.

## Usage

To use this plugin for Enonic XP 7.x, just add the following to your `build.gradle` file:

```gradle
plugins {
  id 'com.enonic.xp.app` version `2.1.0`
}
```

To configure application, use app extension
```
app {
    // Application name - unique application key
    // Default is "${project.group}.${project.name}"
    name = "name.of.my.app"
    
    // The display name of the application
    displayName = "My Application Name"
    
    // Vendor information
    vendorName = "Acme Inc"
    vendorUrl = "http://example.com"
    
    // Minimum supported XP version
    systemVersion = "${xpVersion}"
    
    // By default, the plugin is using these source paths to simplify development in XP `dev` mode: ["${projectDir}/src/main/resources","$buildDir/resources/main" ].
    // This property allows overriding these default paths, for example if you want to include sources from other modules
    devSourcePaths = []
    
    // By default, the plugin prevents applications with non-empty development source paths to be published to maven repositories.
    // Set to `true` to allow applications with development source paths to be published.
    allowDevSourcePathsPublishing = false

    // By default, the plugin generates a jar without version in the file name.
    // Set to `true` to restore Gradle archive naming behaviour.
    keepArchiveFileName = false
    }
```

`com.enonic.xp.app.production` property set to `true` makes development source paths (`X-Source-Paths`) empty regardless of configuration

```
./gradlew clean publish -Pcom.enonic.xp.app.production=true
```

For Enonic XP 6.x, use version 1.2.0.
