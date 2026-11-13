plugins {
    kotlin("jvm") version "2.4.21"
    kotlin("plugin.spring") version "2.4.21"
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("maven-publish")
    id("groovy")

    kotlin("plugin.jpa") version "2.4.21"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":model"))

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

sourceSets {
    test {
        groovy {
            srcDirs("src/test/groovy")
        }
    }
}