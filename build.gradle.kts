plugins {
    id("com.gradle.plugin-publish") version "1.2.0"
}

version = "3.3.0"
group = "com.enonic.gradle"

repositories {
    mavenCentral()
}

dependencies {
    implementation("biz.aQute.bnd:biz.aQute.bnd.gradle:6.4.0")
}

gradlePlugin {
    website.set("https://enonic.com")
    vcsUrl.set("https://github.com/enonic/xp-gradle-plugin")
    plugins {
        create("base_plugin") {
            id = "com.enonic.xp.base"
            displayName = "Enonic XP Base Plugin"
            description = "Base plugin for Enonic XP development."
            tags.set(listOf("enonic", "java", "javascript", "xp"))
            implementationClass = "com.enonic.gradle.xp.BasePlugin"
        }
        create("app_plugin") {
            id = "com.enonic.xp.app"
            displayName = "Enonic XP App Plugin"
            description = "Plugin for Enonic XP application development."
            tags.set(listOf("enonic", "java", "javascript", "xp"))
            implementationClass = "com.enonic.gradle.xp.app.AppPlugin"
        }
    }
}

