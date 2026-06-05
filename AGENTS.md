# AGENTS.md

This file provides guidance to AI agents when working with code in this repository.
## Build Commands

All commands run from the `imagedecoder/` directory:

```bash
# Build and run tests
mvn clean install

# Skip tests
mvn clean install -DskipTests

# Run tests only
mvn test

# Run a single test class
mvn test -Dtest=OpenJpegDecoderTest
mvn test -Dtest=WsqDecoderTest

# Sonar analysis (requires secrets)
mvn verify sonar:sonar -Psonar
```

Build requires Java 21 with `--enable-preview` enabled (configured in both pom.xml files). Tests use the `maven-surefire-plugin` with several `--add-opens` JVM flags.

## Architecture

This is a pure-Java biometric image decoding library for the MOSIP identity platform. It ports two native C codec libraries to Java:

- **JPEG2000** — ported from [OpenJPEG](https://github.com/lessandro/nbis) (`openjp2`)
- **WSQ** — ported from [NBIS WSQ](https://github.com/lessandro/nbis)

### Module Layout

- **`imagedecoder/`** — the published library (`io.mosip.imagedecoder:imagedecoder`)
- **`sample/`** — standalone CLI demo app that reads image files from disk and decodes them

### Core API

The single entry point is `IImageDecoderApi`:

```java
Response<DecoderResponseInfo> decode(DecoderRequestInfo requestInfo);
```

- `DecoderRequestInfo` — takes raw image bytes (`imageData`) and a `isBufferedImage` flag
- `DecoderResponseInfo` — returns image metadata (width, height, DPI, color space, bit rate, compression ratio, lossless flag) plus the decoded pixel data as base64url-encoded bytes and optionally a `BufferedImage`
- `Response<T>` — wraps any response with `statusCode`, `statusMessage`, and `response`

Two implementations:
- `OpenJpegDecoder` — handles JPEG2000 (`.jp2`)
- `WsqDecoder` — handles WSQ (`.wsq`)

### Package Structure (`imagedecoder/src/main/java/io/mosip/imagedecoder/`)

| Package | Purpose |
|---|---|
| `spi/` | Public API interface (`IImageDecoderApi`) |
| `model/` | Request/response models; also C-struct mirrors under `model/openjpeg/` and `model/wsq/` |
| `openjpeg/` | JPEG2000 codec implementation — `OpenJpegDecoder` + many `*Helper` classes |
| `wsq/` | WSQ codec implementation — `WsqDecoder` + many `*Helper` classes |
| `constant/` | Error codes and named constants for both codecs |
| `exceptions/` | `DecoderException` |
| `util/` | `Base64UrlUtil`, `ByteStreamUtil`, `ByteSwapperUtil`, and codec-specific math/image utils |
| `logger/` | Thin wrapper around `kernel-logger-logback` |

### Key Design Details

The codec implementations (`openjpeg/` and `wsq/`) are direct Java ports of C code. The `model/openjpeg/` and `model/wsq/` packages contain Java classes that mirror C structs from the original libraries. `ByteBufferContext` is used to simulate C-style sequential byte reads.

The `*Helper` classes (e.g., `J2KHelper`, `WsqDecoderHelper`) are large stateless utility classes containing the ported algorithm logic. They are called by the `Decoder` classes.

Logging follows the MOSIP convention: `logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, message)`.

### Sample Application

The `sample/` module runs from its `target/` directory after `mvn package`:

```bash
# Decode JPEG2000 files from a folder
java -cp sample-imagedecoder-*.jar;lib\* io.mosip.imagedecoder.sample.SampleImageDecoderApplication \
  "io.mosip.imagedecoder.image.type=0" "io.mosip.imagedecoder.image.folder.path=/BiometricInfo"

# Decode WSQ files from a folder
java -cp sample-imagedecoder-*.jar;lib\* io.mosip.imagedecoder.sample.SampleImageDecoderApplication \
  "io.mosip.imagedecoder.image.type=1" "io.mosip.imagedecoder.image.folder.path=/BiometricInfo"
```

Image type: `0` = JP2000, `1` = WSQ.

### CI/CD

GitHub Actions (`.github/workflows/push-trigger.yml`) triggers on pushes to `master`, `develop*`, `1.*`, `release*`. It reuses shared MOSIP workflows from `mosip/kattu@master-java21` for build, Nexus publish, and Sonar analysis. Publishing to Maven Central uses the `central-publishing-maven-plugin` with `autoPublish=false`.