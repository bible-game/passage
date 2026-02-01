rootProject.name = "passage"

include("service", "model")

dependencyResolutionManagement {
    versionCatalogs {
        repositories {
            mavenLocal()
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/bible-game/version")
                credentials {
                    username = System.getenv("GIT_ACTOR")
                    password = System.getenv("GIT_TOKEN")
                }
            }
        }

        create("libs") {
            from("game.bible:version:0.6.1")
        }
    }
}