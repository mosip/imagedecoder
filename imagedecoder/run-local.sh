#!/usr/bin/env bash
# imagedecoder library local runner — Linux, macOS, Windows Git Bash/MSYS.
# Windows cmd: use run-local.bat (standalone).
#
#   ./run-local.sh init | test | all
set -euo pipefail

MODULE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${MODULE_DIR}/.." && pwd)"
LOCAL_DIR="${MODULE_DIR}/.local"
LOG_DIR="${LOCAL_DIR}/logs"
MODULE="imagedecoder"
UNAME_S="$(uname -s 2>/dev/null || echo unknown)"

MVN_SKIP=(
  "-DskipTests"
  "-Dgpg.skip=true"
  "-Dmaven.javadoc.skip=true"
)

usage() {
  cat <<EOF
Local imagedecoder library

  Linux / macOS / Git Bash:
    ./run-local.sh init | test | all

  Windows cmd:
    run-local.bat init | test | all

  all = init + test
EOF
  exit "${1:-0}"
}

need_cmd() {
  command -v "$1" >/dev/null 2>&1 || {
    echo "error: '$1' is required on PATH" >&2
    exit 1
  }
}

ensure_dirs() {
  mkdir -p "$LOG_DIR"
}

check_prereqs() {
  need_cmd java
  need_cmd mvn
  echo "os: ${UNAME_S}"
  local ver
  ver="$(java -version 2>&1 | head -n 1 || true)"
  echo "java: $ver"
  if ! echo "$ver" | grep -E '"21[\. "]' >/dev/null 2>&1; then
    echo "warn: JDK 21 is required. Continuing anyway." >&2
  fi
}

cmd_init() {
  check_prereqs
  ensure_dirs
  echo "==> packaging ${MODULE} (skip tests)"
  (
    cd "$REPO_ROOT"
    mvn clean install "${MVN_SKIP[@]}" -pl imagedecoder -am
  )
  echo "init complete"
}

cmd_test() {
  check_prereqs
  echo "==> maven tests"
  (
    cd "$REPO_ROOT"
    mvn test "-Dgpg.skip=true" "-Dmaven.javadoc.skip=true" -pl imagedecoder
  )
}

cmd_all() {
  echo "==> all: init + test"
  cmd_init
  cmd_test
}

main() {
  local cmd="${1:-}"
  shift || true
  case "$cmd" in
    -h|--help|help) usage 0 ;;
    init) cmd_init ;;
    test) cmd_test ;;
    all) cmd_all ;;
    "") usage 1 ;;
    *) echo "error: unknown command '$cmd'" >&2; usage 1 ;;
  esac
}

main "$@"
