#!/usr/bin/env bash
# Decode JPEG2000 (.jp2) samples. Package sample first: mvn clean package -DskipTests -Dgpg.skip=true
set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if compgen -G "${DIR}/target/sample-imagedecoder-*.jar" >/dev/null; then
  cd "${DIR}/target"
else
  cd "${DIR}"
fi
JAR=""
for f in sample-imagedecoder-*.jar; do
  [[ -f "$f" ]] || continue
  case "$f" in *sources*|*javadoc*|*with-dependencies*) continue ;; esac
  JAR="$f"
  break
done
if [[ -z "$JAR" ]]; then
  echo "error: sample jar not found. Package sample first: mvn clean package -DskipTests -Dgpg.skip=true" >&2
  exit 1
fi
if [[ ! -d lib ]]; then
  echo "error: lib/ missing. Package sample first: mvn clean package -DskipTests -Dgpg.skip=true" >&2
  exit 1
fi
exec java -cp "${JAR}:lib/*" \
  io.mosip.imagedecoder.sample.SampleImageDecoderApplication \
  "io.mosip.imagedecoder.image.type=0" \
  "io.mosip.imagedecoder.image.folder.path=/BiometricInfo"
