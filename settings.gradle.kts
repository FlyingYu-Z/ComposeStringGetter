rootProject.name = "ComposeStringGetter"
pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://maven.xillio.com/artifactory/libs-release/")
        gradlePluginPortal()
    }
}
buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://plugins.gradle.org/m2/")
    }
}

