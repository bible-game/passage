plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
    id("maven-publish")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        implementation(rootProject.libs.bundles.app)
        implementation(rootProject.libs.bundles.core)
        implementation(rootProject.libs.bundles.data)
        implementation(rootProject.libs.bundles.database)
        implementation(rootProject.libs.bundles.kotlin)
        implementation(rootProject.libs.bundles.spring)
        implementation(rootProject.libs.bundles.test)
    }
}
