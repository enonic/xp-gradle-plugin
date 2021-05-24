plugins {
    `maven-publish`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.15.0"
}

version = "3.1.0-SNAPSHOT"
group = "com.enonic.gradle"

repositories {
    mavenCentral()
}

dependencies {
    implementation("biz.aQute.bnd:biz.aQute.bnd.gradle:5.3.0")
}

gradlePlugin {
    plugins {
        register("base_plugin") {
            id = "com.enonic.xp.base"
            implementationClass = "com.enonic.gradle.xp.BasePlugin"
        }
        register("app_plugin") {
            id = "com.enonic.xp.app"
            implementationClass = "com.enonic.gradle.xp.app.AppPlugin"
        }
    }
}

pluginBundle {
    website = "https://enonic.com"
    vcsUrl = "https://github.com/enonic/xp-gradle-plugin"
    (plugins) {
        "base_plugin" {
            displayName = "Enonic XP Base Plugin"
            description = "Base plugin for Enonic XP development."
            tags = listOf("enonic", "java", "javascript", "xp")
        }
        "app_plugin" {
            displayName = "Enonic XP App Plugin"
            description = "Plugin for Enonic XP application development."
            tags = listOf("enonic", "java", "javascript", "xp")
        }
    }
}
