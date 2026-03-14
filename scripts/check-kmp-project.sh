#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "$0")/.." && pwd)"
kmp_root="$repo_root/apps/kmp"

declare -a paths=(
  "apps/kmp/gradlew"
  "apps/kmp/gradle/wrapper/gradle-wrapper.jar"
  "apps/kmp/gradle/wrapper/gradle-wrapper.properties"
  "apps/kmp/settings.gradle.kts"
  "apps/kmp/build.gradle.kts"
  "apps/kmp/gradle.properties"
  "apps/kmp/gradle/libs.versions.toml"
  "apps/kmp/shared-domain/build.gradle.kts"
  "apps/kmp/shared-data/build.gradle.kts"
  "apps/kmp/androidApp/build.gradle.kts"
  "apps/kmp/androidApp/src/main/AndroidManifest.xml"
  "apps/kmp/androidApp/src/main/kotlin/com/telegram/compare/kmp/android/MainActivity.kt"
  "apps/kmp/docs/ai-workflow.md"
  "apps/kmp/docs/module-map.md"
  "apps/kmp/docs/debug-runbook.md"
)

failed=0

echo "[KMP project]"

for path in "${paths[@]}"; do
  if [[ -e "$repo_root/$path" ]]; then
    echo "[ok] $path"
  else
    echo "[missing] $path"
    failed=1
  fi
done

if [[ -x "$kmp_root/gradlew" ]]; then
  echo "[ok] apps/kmp/gradlew is executable"
else
  echo "[missing] apps/kmp/gradlew executable bit"
  failed=1
fi

exit "$failed"
