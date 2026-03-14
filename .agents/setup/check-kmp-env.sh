#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "$0")/../.." && pwd)"

echo "[KMP env]"
bash "$(dirname "$0")/check-android-env.sh"

if command -v kotlinc >/dev/null 2>&1; then
  echo "kotlinc: $(command -v kotlinc)"
else
  echo "kotlinc: optional (Gradle and Kotlin plugin can still compile project Kotlin sources)"
fi

if [[ -x "$repo_root/apps/kmp/gradlew" ]]; then
  echo "gradle-wrapper: ready  $repo_root/apps/kmp/gradlew"
else
  echo "gradle-wrapper: missing"
fi
