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
    // application name - unique application key
    name = "name.of.your.app"
    
    // The display name of the application
    displayName = "My Application Name"
    
    // vendor information
    vendorName = "Acme Inc"
    vendorUrl = "http://example.com"
    
    // minimum supported XP version
    systemVersion = "${xpVersion}"
    
    // By default, plugin generates a jar without version in file name.
    // Set to true, to restore Gradle archive naming behaviour.
    keepArchiveFileName = false
}
```

By default, applications are built with development source paths included.  
To exclude development source paths from the application set `com.enonic.xp.app.production` property to `true`.

For Enonic XP 6.x, use version 1.2.0.
