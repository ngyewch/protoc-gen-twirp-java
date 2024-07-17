plugins {
    `maven-publish`
    signing
}

group = "com.github.ngyewch"
version = "0.1.1"

configurations {
    create("binaries")
}

val binaryArtifacts: MutableList<PublishArtifact> = ArrayList()

tasks {
    register("buildBinaries")

    val buildInputs = arrayOf(
        arrayOf("linux", "arm64", "linux-aarch_64"),
        arrayOf("linux", "ppc64le", "linux-ppcle_64"),
        arrayOf("linux", "s390x", "linux-s390_64"),
        arrayOf("linux", "386", "linux-x86_32"),
        arrayOf("linux", "amd64", "linux-x86_64"),
        arrayOf("darwin", "arm64", "osx-aarch_64"),
        arrayOf("darwin", "amd64", "osx-x86_64"),
        arrayOf("windows", "386", "windows-x86_32"),
        arrayOf("windows", "amd64", "windows-x86_64"),
    )

    fun createTask(goos: String, goarch: String, classifier: String) {
        val taskName = "build_${goos}_${goarch}"
        val outputExeFile =
            layout.buildDirectory.file("protoc-gen-twirp-java-${project.version}-${classifier}.exe").get().asFile
        val outputAscFile =
            layout.buildDirectory.file("protoc-gen-twirp-java-${project.version}-${classifier}.exe.asc").get().asFile

        binaryArtifacts.add(artifacts.add("binaries", outputExeFile) {
            this.builtBy(taskName)
            this.classifier = classifier
            this.extension = "exe"
        })
        binaryArtifacts.add(artifacts.add("binaries", outputAscFile) {
            this.builtBy(taskName)
            this.classifier = "${classifier}.exe"
            this.extension = "asc"
        })

        register<Exec>(taskName) {
            dependsOn("createBuildDirectory")
            outputs.file(outputExeFile)
            outputs.file(outputAscFile)
            commandLine("go", "build", "-o", outputExeFile)
            environment("GOOS", goos)
            environment("GOARCH", goarch)
        }

        named("buildBinaries") {
            dependsOn(taskName)
        }
    }

    for (buildInput in buildInputs) {
        createTask(buildInput[0], buildInput[1], buildInput[2])
    }

    register<Exec>("createBuildDirectory") {
        commandLine("mkdir", "-p", layout.buildDirectory.get().asFile.path)
    }

    withType<PublishToMavenRepository>().configureEach {
        dependsOn("buildBinaries")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String
            artifactId = project.name
            version = project.version as String

            for (binaryArtifact in binaryArtifacts) {
                artifact(binaryArtifact)
            }

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
    repositories {
        maven {
            name = "build"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["maven"])
}
