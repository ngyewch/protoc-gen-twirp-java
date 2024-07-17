![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/ngyewch/protoc-gen-twirp-java/CI.yml)
![GitHub tag (with filter)](https://img.shields.io/github/v/tag/ngyewch/protoc-gen-twirp-java)
![Maven Central Version](https://img.shields.io/maven-central/v/io.github.ngyewch/protoc-gen-twirp-java)

# protoc-gen-twirp-java

[Twirp](https://github.com/twitchtv/twirp) protobuf generator for Java.

## Usage notes

**IMPORTANT** Declare `option go_package` in your `.proto` files even if you do not plan to generate Go source.

## Example project

See [github.com/ngyewch/protoc-gen-twirp-java-example](https://github.com/ngyewch/protoc-gen-twirp-java-example)

## Usage

### `build.gradle.kts`
```
import com.google.protobuf.gradle.id

plugins {
    id("com.google.protobuf") version "0.9.4"
    // ...
}

dependencies {
    // Protobuf
    implementation(platform("com.google.protobuf:protobuf-bom:4.27.2"))
    implementation("com.google.protobuf:protobuf-java")
    implementation("com.google.protobuf:protobuf-java-util")

    // Helidon
    implementation(platform("io.helidon:helidon-bom:2.6.7"))

    // Helidon client
    implementation("io.helidon.webclient:helidon-webclient")

    // Helidon server
    implementation("io.helidon.common:helidon-common-http")
    implementation("io.helidon.common:helidon-common-reactive")
    implementation("io.helidon.media:helidon-media-common")
    implementation("io.helidon.webserver:helidon-webserver")

    // ...
}

protobuf {
    plugins {
        id("twirp-java") {
            artifact = "io.github.ngyewch:protoc-gen-twirp-java:0.1.1"
        }
    }
    protoc {
        artifact = "com.google.protobuf:protoc:4.27.2"
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("twirp-java") {
                    option("gen-helidon-client=true")
                    option("gen-helidon-server=true")
                }
            }
        }
    }
}

// ...
```

## Options

| Name                 | Type      | Default | Description                                                                                            |
|----------------------|-----------|---------|--------------------------------------------------------------------------------------------------------|
| `gen-helidon-client` | `boolean` | `false` | Generate [Helidon SE WebClient based](https://helidon.io/docs/v2/se/webclient/01_introduction) client. | 
| `gen-helidon-server` | `boolean` | `false` | Generate [Helidon SE WebServer based](https://helidon.io/docs/v2/se/webserver/01_introduction) server. | 
