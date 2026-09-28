# imagedecoder (library)

Pure-Java JPEG2000 + WSQ biometric image decoder for MOSIP.

- Artifact: `io.mosip.imagedecoder:imagedecoder`
- Parent: `imagedecoder-parent` (Spring Boot **4.1.1**, no `kernel-bom`)
- Depends on: `kernel-core` (version from parent `kernel.core.version`; Logfactory included)

```text
spi/ → IImageDecoderApi
openjpeg/ → OpenJpegDecoder
wsq/ → WsqDecoder
```

## Local runner

From **`imagedecoder/`**:

```bat
run-local.bat init
run-local.bat test
run-local.bat all
```

```bash
chmod +x run-local.sh
./run-local.sh init
./run-local.sh test
./run-local.sh all
```

| Command | Action |
| ------- | ------ |
| `init` | Install parent + package this module (skip tests) |
| `test` | Run unit tests (`OpenJpegDecoderTest`, `WsqDecoderTest`) |
| `all` | `init` + `test` |

See root [README.md](../README.md) and [AGENTS.md](../AGENTS.md).
