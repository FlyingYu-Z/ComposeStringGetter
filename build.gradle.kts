plugins {
    kotlin("jvm") version "2.2.0"
    id("org.jetbrains.compose") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    id("org.jetbrains.intellij") version "1.16.0"
}

group = "cn.beingyi"
version = "1.0.0"

subprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
}

repositories {
    mavenCentral()
    google()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}
dependencies {
    //implementation("org.jetbrains.kotlin:kotlin-stdlib")
    //implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
    //implementation("org.jetbrains.compose.material:material:")
    //implementation("org.jetbrains.compose:preview-rpc")
    implementation(compose.desktop.currentOs)
    implementation("androidx.compose.material:material-icons-extended:1.6.8")

    implementation("org.dom4j:dom4j:2.1.1")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("com.alibaba:fastjson:1.2.76")
    implementation("com.squareup:kotlinpoet:1.9.0")


//    val IntellijCompilePath="/home/flying/Applications/idea-IU-221.5080.210"
//    compileOnly(fileTree(mapOf("dir" to "$IntellijCompilePath/plugins/java/lib", "include" to listOf("*.jar"))))
//    compileOnly(fileTree(mapOf("dir" to "$IntellijCompilePath/lib", "include" to listOf("*.jar"))))
//
//    testCompileOnly(fileTree(mapOf("dir" to "$IntellijCompilePath/plugins/java/lib", "include" to listOf("*.jar"))))
//    testCompileOnly(fileTree(mapOf("dir" to "$IntellijCompilePath/lib", "include" to listOf("*.jar"))))
//
//    testCompileOnly(group= "junit", name= "junit", version= "4.12")

}

// Configure Gradle IntelliJ Plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
//    version.set("2024.2.2")
    //type.set("IC") // Target IDE Platform
    type.set("AI") // Target IDE Platform

    plugins.set(
        listOf(
            "java",
            "com.intellij.gradle",

//            "org.jetbrains.kotlin"
//            `kotlin`
        )
    )
//    localPath = "/home/flying/Applications/idea-IU-243.22562.218"
    localPath = "/home/flying/Applications/android-studio/"
}

tasks.buildSearchableOptions {
    // temporary workaround
    enabled = false
}

kotlin{
    jvmToolchain(17)
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
        }
    }

//    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//        kotlinOptions.jvmTarget = "17"
//    }

    patchPluginXml {
        sinceBuild.set("243")
        untilBuild.set("252.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

if (hasProperty("buildScan")) {
    extensions.findByName("buildScan")?.withGroovyBuilder {
        setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
        setProperty("termsOfServiceAgree", "yes")
    }
}