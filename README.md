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

For Enonic XP 6.x, use version 1.2.0.
