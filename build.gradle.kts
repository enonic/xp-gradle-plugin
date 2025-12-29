plugins {
    alias(libs.plugins.plugin.publish)
}

version = "4.0.0-SNAPSHOT"
group = "com.enonic.gradle"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bnd.gradle)

    testImplementation(platform(libs.junit.bom))
    testImplementation(platform(libs.mockito.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)
    testImplementation(libs.mockito.jupiter)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
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

