rootProject.name = "passage"

include("service", "model")

dependencyResolutionManagement {
    versionCatalogs {
        repositories {
            mavenLocal()
        }

        create("libs") {
            from("game.bible:version:0.0.1-SNAPSHOT")
        }
    }
}