# Image Decoder

[![Maven Package upon a push](https://github.com/mosip/imagedecoder/actions/workflows/push-trigger.yml/badge.svg)](https://github.com/mosip/imagedecoder/actions/workflows/push-trigger.yml)
[![License: MPL 2.0](https://img.shields.io/badge/License-MPL_2.0-brightgreen.svg)](LICENSE)

## Overview

**Image Decoder** is a pure-Java biometric image decoding library for the [MOSIP](https://mosip.io) platform. It decodes **JPEG2000** (`.jp2`) and **WSQ** (`.wsq`) payloads without native C codecs.

This repository publishes a **library JAR** (`io.mosip.imagedecoder:imagedecoder`). The `sample/` module is a local CLI demo and is **not** published to Maven Central.

Parent Maven coordinates: `io.mosip.imagedecoder:imagedecoder-parent` (see root `pom.xml` for the current version).

## Features

- **JPEG2000 decode** — Java port of OpenJPEG (`openjp2`)
- **WSQ decode** — Java port of NBIS WSQ
- **Metadata + pixels** — width, height, DPI, color space, bit rate, compression ratio, lossless flag, base64url image data, optional `BufferedImage`
- **No native libraries** — runs on stock JDK 21

## Inspired by

- [NBIS WSQ](https://github.com/lessandro/nbis) — WSQ decoder
- [NBIS openjp2](https://github.com/lessandro/nbis) — JPEG2000 decoder

## Engineering standards

| Item | Value |
| ---- | ----- |
| Java | 21 |
| Maven | 3.9+ |
| Spring Boot | **4.1.1** (via `spring-boot-starter-parent`) |
| BOM policy | **No `kernel-bom`** — versions pinned in the parent POM |
| Commons dependency | `io.mosip.kernel:kernel-core` (version: `kernel.core.version` in parent POM) |
| Logging | `Logfactory` inside **kernel-core** (do **not** add `kernel-logger-logback`) |
| Jackson | Jackson **2** via `spring-boot-jackson2` |
| Coverage gate | JaCoCo **≥ 85%** (only `config/`, `dto/`, `entity/` excluded; see parent POM) |
| License | [Mozilla Public License 2.0](LICENSE) |
## Modules

| Module | Artifact | Description |
| ------ | -------- | ----------- |
| [imagedecoder](imagedecoder/README.md) | `io.mosip.imagedecoder:imagedecoder` | Published JPEG2000 + WSQ codec library |
| [sample](sample/) | `io.mosip.imagedecoder:sample-imagedecoder` | CLI demo (**not** in reactor / not published) |

```text
imagedecoder-parent
└─ imagedecoder          # published library

sample/                  # standalone CLI (parent POM only; not a reactor module)
```

## Core API

```java
Response<DecoderResponseInfo> decode(DecoderRequestInfo requestInfo);
```

| Type | Role |
| ---- | ---- |
| `DecoderRequestInfo` | Raw `imageData` bytes + `isBufferedImage` flag |
| `DecoderResponseInfo` | Metadata + base64url pixels + optional `BufferedImage` |
| `Response<T>` | `statusCode`, `statusMessage`, `response` |
| `OpenJpegDecoder` | JPEG2000 (`.jp2`) |
| `WsqDecoder` | WSQ (`.wsq`) |

## Prerequisites

- **JDK:** 21
- **Maven:** 3.9.6 or higher
- **Git**
- **commons `kernel-core`** (version matching `kernel.core.version` in the parent POM) installed locally (or available from your snapshot repo):

  ```text
  cd ../commons/kernel
  mvn clean install -Dgpg.skip=true -pl kernel-core -am
  ```

Do **not** introduce `kernel-bom`. Do **not** add `kernel-logger-logback`.

## Installation

### Clone

```text
git clone https://github.com/mosip/imagedecoder.git
cd imagedecoder
```

### Build library

```text
mvn clean install -Dgpg.skip=true
```

### Skip tests / GPG (local)

```text
mvn clean install -Dgpg.skip=true -DskipTests
```

### Run tests

```text
mvn test
mvn test -Dtest=OpenJpegDecoderTest
mvn test -Dtest=WsqDecoderTest
```

### Coverage

```text
mvn clean verify -Dgpg.skip=true
# report: imagedecoder/target/site/jacoco/index.html
# gate: ≥90% instruction coverage (excludes model/constant/*Helper/stream utils)
```

### Consume as a dependency

Pin `<version>` from the root `pom.xml` (it changes across releases):

```xml
<dependency>
  <groupId>io.mosip.imagedecoder</groupId>
  <artifactId>imagedecoder</artifactId>
  <version>…</version>
</dependency>
```

## Local build (`run-local`)

Working directory: **`imagedecoder/`**.

```bat
REM Windows
cd imagedecoder
run-local.bat init
run-local.bat test
run-local.bat all
```

```bash
# Linux / macOS / Git Bash
cd imagedecoder
chmod +x run-local.sh
./run-local.sh init
./run-local.sh test
./run-local.sh all
```

| Command | Action |
| ------- | ------ |
| `init` | Install parent + package library (skip tests) |
| `test` | Run unit tests |
| `all` | `init` + `test` |

## Sample CLI

Working directory: **`sample/`**. Sample images live under `sample/BiometricInfo/`.

```text
cd sample
mvn clean package -DskipTests -Dgpg.skip=true
```

| Windows | Unix |
| ------- | ---- |
| `run-jp2000-decoder.bat` | `./run-jp2000-decoder.sh` |
| `run-wsq-decoder.bat` | `./run-wsq-decoder.sh` |

Image type argument: `0` = JPEG2000, `1` = WSQ.

## License & notices

- Product license: [MPL-2.0](LICENSE)
- Third-party attribution: [NOTICE](NOTICE), [THIRD-PARTY-NOTICES](THIRD-PARTY-NOTICES), [licenses/](licenses/)

## CI/CD

GitHub Actions (`.github/workflows/push-trigger.yml`) triggers on pushes to `master`, `develop*`, `1.*`, `release*`, and related branches. It reuses shared MOSIP workflows from `mosip/kattu@master-java21` for build, Nexus publish, and Sonar analysis. Publishing to Maven Central uses `central-publishing-maven-plugin` with `autoPublish=false`.
