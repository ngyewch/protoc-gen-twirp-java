plugins {
    `java-library`
    `maven-publish`
    signing
    id("com.diffplug.spotless")
    id("com.gradleup.nmcp")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    api(platform(project(":twirp-bom")))

    implementation(project(":twirp-common"))
    implementation(project(":twirp-helidon-common"))

    api("com.google.protobuf:protobuf-java")
    implementation("com.google.protobuf:protobuf-java-util")
    api("io.helidon.common:helidon-common-http")
    implementation("io.helidon.common:helidon-common-reactive")
    implementation("io.helidon.media:helidon-media-common")
    implementation("io.helidon.webclient:helidon-webclient")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String
            artifactId = project.name
            version = project.version as String

            from(components["java"])

            pom {
                name = project.name
                description = "Twirp Helidon client."
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

spotless {
    java {
        googleJavaFormat("1.23.0").reflowLongStrings().skipJavadocFormatting()
        formatAnnotations()
        targetExclude("build/**")
    }
}

nmcp {
    publish("maven") {
        username = project.properties["mavenCentralUsername"] as String?
        password = project.properties["mavenCentralPassword"] as String?
        publicationType = "AUTOMATIC"
    }
}
