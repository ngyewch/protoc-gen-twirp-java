## Publish locally to `build/repo`

```
task publishLocally
```

## Publishing to Maven Central Portal

```
./gradlew :protoc-gen-twirp-java:publishAllPublicationsToCentralPortal
./gradlew :twirp-bom:publishAllPublicationsToCentralPortal
```

Wait for `io.github.ngyewch.twirp:twirp-bom` to be `PUBLISHED` at Maven Central Portal.

```
./gradlew :twirp-common:publishAllPublicationsToCentralPortal
```

Wait for `io.github.ngyewch.twirp:twirp-common` to be `PUBLISHED` at Maven Central Portal.

```
./gradlew :twirp-helidon-common:publishAllPublicationsToCentralPortal
```

Wait for `io.github.ngyewch.twirp:twirp-helidon-common` to be `PUBLISHED` at Maven Central Portal.

```
./gradlew :twirp-helidon-client:publishAllPublicationsToCentralPortal
./gradlew :twirp-helidon-server:publishAllPublicationsToCentralPortal
```
