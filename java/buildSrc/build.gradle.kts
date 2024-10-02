plugins {
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.17.0")
}

gradlePlugin {
    plugins {
        create("protocPlugin") {
            id = "io.github.ngyewch.protoc.plugin"
            implementationClass = "io.github.ngyewch.protoc.plugin.ProtocPlugin"
        }
    }
}
