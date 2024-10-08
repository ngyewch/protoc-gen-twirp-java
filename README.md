![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/ngyewch/protoc-gen-twirp-java/CI.yml)
![GitHub tag (with filter)](https://img.shields.io/github/v/tag/ngyewch/protoc-gen-twirp-java)
![Maven Central Version](https://img.shields.io/maven-central/v/io.github.ngyewch.twirp/protoc-gen-twirp-java)

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
    implementation(platform("io.github.ngyewch.twirp:twirp-bom:0.3.0"))

    // Protobuf
    implementation("com.google.protobuf:protobuf-java")

    // Common
    implementation("io.github.ngyewch.twirp:twirp-common")
    
    // Apache HttpComponents
    implementation("io.github.ngyewch.twirp:twirp-apache-client")

    // Helidon
    implementation("io.github.ngyewch.twirp:twirp-helidon-common")

    // Helidon client
    implementation("io.github.ngyewch.twirp:twirp-helidon-client")

    // Helidon server
    implementation("io.github.ngyewch.twirp:twirp-helidon-server")
    implementation("io.helidon.common:helidon-common-http")
    implementation("io.helidon.common:helidon-common-reactive")
    implementation("io.helidon.webserver:helidon-webserver")

    // ...
}

protobuf {
    plugins {
        id("twirp-java") {
            artifact = "io.github.ngyewch.twirp:protoc-gen-twirp-java:0.3.0"
        }
    }
    protoc {
        artifact = "com.google.protobuf:protoc:4.28.2"
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("twirp-java") {
                    option("gen-helidon-client=true")
                    option("gen-helidon-server=true")
                    option("gen-apache-client=true")
                }
            }
        }
    }
}

// ...
```

## Options

| Name                 | Type      | Default | Description                                                                                                  |
|----------------------|-----------|---------|--------------------------------------------------------------------------------------------------------------|
| `gen-helidon-client` | `boolean` | `false` | Generate [Helidon SE WebClient](https://helidon.io/docs/v2/se/webclient/01_introduction) based client.       | 
| `gen-helidon-server` | `boolean` | `false` | Generate [Helidon SE WebServer](https://helidon.io/docs/v2/se/webserver/01_introduction) based server.       | 
| `gen-apache-client`  | `boolean` | `false` | Generate [Apache HttpComponents](https://hc.apache.org/httpcomponents-client-4.5.x/index.html) based client. | 
