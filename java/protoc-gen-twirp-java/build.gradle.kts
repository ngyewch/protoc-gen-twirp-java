plugins {
    `maven-publish`
    signing
    id("com.gradleup.nmcp") version "0.0.9"
    id("io.github.ngyewch.protoc.plugin")
    //id("com.autonomousapps.dependency-analysis") version "1.32.0"
    //id("com.diffplug.spotless") version "6.25.0"
    //id("com.github.ben-manes.versions") version "0.51.0"
    //id("se.ascp.gradle.gradle-versions-filter") version "0.1.16"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String
            artifactId = project.name
            version = project.version as String

            from(components["protocPlugin"])

            pom {
                name = project.name
                description = "Twirp protobuf generator for Java."
                url = "https://github.com/ngyewch/protoc-gen-twirp-java"
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://github.com/ngyewch/protoc-gen-twirp-java/blob/main/LICENSE"
                    }
                }
                scm {
                    connection = "scm:git:git@github.com:ngyewch/protoc-gen-twirp-java.git"
                    developerConnection = "scm:git:git@github.com:ngyewch/protoc-gen-twirp-java.git"
                    url = "https://github.com/ngyewch/protoc-gen-twirp-java"
                }
                developers {
                    developer {
                        id.set("ngyewch")
                        name.set("Nick Ng")
                    }
                }
            }
        }
    }
    /*
    repositories {
        maven {
            name = "build"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
    */
}

signing {
    useGpgCmd()
    sign(publishing.publications["maven"])
}

/*
nmcp {
    publishAllPublications {
        username = project.properties["mavenCentralUsername"] as String?
        password = project.properties["mavenCentralPassword"] as String?
        publicationType = "AUTOMATIC"
    }
}
*/