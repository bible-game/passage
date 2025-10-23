rootProject.name = "passage"

include("service", "model")

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/bible-game/version")
            credentials {
                username = System.getenv("GH_PACKAGES_USER") ?: System.getenv("GITHUB_ACTOR")
                password = System.getenv("GH_PACKAGES_TOKEN") ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }

    versionCatalogs {
        create("libs") {
            from("game.bible:version:0.6.1")
        }
    }
}
