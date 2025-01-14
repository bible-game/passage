plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.jetbrains.kotlin.plugin.noarg") version "2.1.20-Beta1"
    // TODO :: apply from ver cat
    id("maven-publish")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

//    implementation("game.bible:common:0.0.1-SNAPSHOT")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}