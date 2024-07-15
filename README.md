![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/ngyewch/protoc-gen-twirp-java/CI.yml)
![GitHub tag (with filter)](https://img.shields.io/github/v/tag/ngyewch/protoc-gen-twirp-java)

# protoc-gen-twirp-java

[Twirp](https://github.com/twitchtv/twirp) protobuf generator for Java.

## Usage notes

**IMPORTANT** Declare `option go_package` in your `.proto` files even if you do not plan to generate Go source.

## Example project

See [github.com/ngyewch/protoc-gen-twirp-java-example](https://github.com/ngyewch/protoc-gen-twirp-java-example)

## Installation

```
go install github.com/ngyewch/protoc-gen-twirp-java@latest
```

## Generating sources

Generated sources will need to be added via source set configuration. For example:

```
sourceSets {
    main {
        java {
            srcDir("build/generated/source/protoc-gen-twirp-java/main/java")
        }
    }
}
```

### Command-line

```
OUTPUT_DIR=build/generated/source/protoc-gen-twirp-java/main/java
mkdir -p ${OUTPUT_DIR}
protoc --proto_path=${PB_DIR} \
    --twirp-java_out=${OUTPUT_DIR} \
    --twirp-java_opt=gen-helidon-client=true \
    --twirp-java_opt=gen-helidon-server=true \
    ${PB_FILE}
```

### Via Docker

```
docker build --tag go-protoc-twirp-java:latest https://github.com/ngyewch/protoc-gen-twirp-java.git

OUTPUT_DIR=build/generated/source/protoc-gen-twirp-java/main/java
mkdir -p ${OUTPUT_DIR}
docker run --rm -it \
    --user $(id -u):$(id -g) \
    -v ${PB_DIR}:/protobuf \
    -v ${OUTPUT_DIR}:/build \
    go-protoc-twirp-java:latest \
    protoc --proto_path=/protobuf \
    --twirp-java_out=/build \
    --twirp-java_opt=gen-helidon-client=true \
    --twirp-java_opt=gen-helidon-server=true \
    ${PB_FILE}
```

## Options

| Name                 | Type      | Default | Description                                                                                            |
|----------------------|-----------|---------|--------------------------------------------------------------------------------------------------------|
| `gen-helidon-client` | `boolean` | `false` | Generate [Helidon SE WebClient based](https://helidon.io/docs/v2/se/webclient/01_introduction) client. | 
| `gen-helidon-server` | `boolean` | `false` | Generate [Helidon SE WebServer based](https://helidon.io/docs/v2/se/webserver/01_introduction) server. | 

## Java dependencies

```
implementation("com.google.protobuf:protobuf-java:3.25.1")
implementation("com.google.protobuf:protobuf-java-util:3.25.1")
```

### Helidon Client

```
implementation(platform("io.helidon:helidon-bom:2.6.4"))

implementation("io.helidon.webclient:helidon-webclient")
```

### Helidon Server

```
implementation(platform("io.helidon:helidon-bom:2.6.4"))

implementation("io.helidon.common:helidon-common-http")
implementation("io.helidon.common:helidon-common-reactive")
implementation("io.helidon.media:helidon-media-common")
implementation("io.helidon.webserver:helidon-webserver")
```
