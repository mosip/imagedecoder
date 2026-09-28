# sample-imagedecoder

CLI demos for JPEG2000 and WSQ decoding. **Not** in the Maven reactor / not published.

Package first (from repo root or after library `run-local init`):

```text
cd sample
mvn clean package -DskipTests -Dgpg.skip=true
```

Then:

```bat
run-jp2000-decoder.bat
run-wsq-decoder.bat
```

```bash
chmod +x run-*-decoder.sh
./run-jp2000-decoder.sh
./run-wsq-decoder.sh
```

Library build/test: use `imagedecoder/run-local.(bat|sh)`. See root [README.md](../README.md).
