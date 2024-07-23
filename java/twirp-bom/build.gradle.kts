plugins {
    `java-platform`
    `maven-publish`
    signing
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform("com.google.protobuf:protobuf-bom:4.27.2"))
    api(platform("io.helidon:helidon-bom:2.6.7"))

    constraints {
        api(project(":twirp-common"))
        api(project(":twirp-helidon-common"))
        api(project(":twirp-helidon-client"))
        api(project(":twirp-helidon-server"))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String
            artifactId = project.name
            version = project.version as String

            from(components["javaPlatform"])

            pom {
                name = project.name
                description = "Twirp BOM."
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
}

signing {
    useGpgCmd()
    sign(publishing.publications["maven"])
}
