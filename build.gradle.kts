plugins {
    id("com.gradle.plugin-publish") version "1.3.1"
}

version = "3.6.2-SNAPSHOT"
group = "com.enonic.gradle"

repositories {
    mavenCentral()
}

dependencies {
    implementation("biz.aQute.bnd:biz.aQute.bnd.gradle:6.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("org.mockito:mockito-junit-jupiter:5.18.0")
}

tasks.compileJava {
    options.release.set(11)
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

