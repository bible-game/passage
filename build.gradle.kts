import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("groovy")
    id("io.spring.dependency-management") version "1.1.6"
    id("net.researchgate.release") version "3.0.2"
    id("maven-publish")
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"

    alias(libs.plugins.spring.boot)
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}


subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")

        implementation("org.projectlombok:lombok:1.18.36")
//        implementation("game.bible:common:0.0.1-SNAPSHOT")

        api(libs.bundles.core)
    }

}
