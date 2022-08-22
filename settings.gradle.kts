rootProject.name = "ComposeStringGetter"

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://plugins.gradle.org/m2/")

    }
    dependencies {
        classpath("org.jetbrains.compose:compose-gradle-plugin:1.2.0-alpha01-dev682")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
    }
}
