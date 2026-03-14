#!/usr/bin/env bash
set -euo pipefail

echo "[KMP env]"
bash "$(dirname "$0")/check-android-env.sh"

if command -v kotlinc >/dev/null 2>&1; then
  echo "kotlinc: $(command -v kotlinc)"
else
  echo "kotlinc: missing"
fi
