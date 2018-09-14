# XP Gradle Plugin

[![Build Status](https://travis-ci.org/enonic/xp-gradle-plugin.svg?branch=master)](https://travis-ci.org/enonic/xp-gradle-plugin)
[![License](https://img.shields.io/github/license/enonic/xp-gradle-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

This Gradle plugin does the following:

* Build application jar files for Enonic XP (`com.enonic.xp.app`).
* Possible to provision and run Enonic XP with your app deployed (`com.enonic.xp.run`).
* Creates documentation for the plugin (`com.enonic.xp.doc`).
* Configures easy steps for deploying artifacts to a repo.

## Usage

To use this plugin, just add the following to your `build.gradle` file:

```gradle
plugins {
  id 'com.enonic.xp.app` version `1.2.0`
}
```
