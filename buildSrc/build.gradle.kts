plugins {
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        create("protocPlugin") {
            id = "io.github.ngyewch.protoc.plugin"
            implementationClass = "io.github.ngyewch.protoc.plugin.ProtocPlugin"
        }
    }
}