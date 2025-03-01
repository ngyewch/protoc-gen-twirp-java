plugins {
    `maven-publish`
    id("com.autonomousapps.dependency-analysis") version "2.8.0"
    id("com.diffplug.spotless") version "7.0.2"
    id("com.github.ben-manes.versions") version "0.52.0"
    id("com.gradleup.nmcp") version "0.0.9"
    id("se.ascp.gradle.gradle-versions-filter") version "0.1.16"
}

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.ben-manes.versions")
    apply(plugin = "se.ascp.gradle.gradle-versions-filter")

    publishing {
        repositories {
            maven {
                name = "build"
                url = uri(rootProject.layout.buildDirectory.dir("repo"))
            }
        }
    }

    versionsFilter {
        gradleReleaseChannel.set("current")
        checkConstraints.set(true)
        outPutFormatter.set("json")
    }
}

versionsFilter {
    gradleReleaseChannel.set("current")
    checkConstraints.set(true)
    outPutFormatter.set("json")
}
