#!/usr/bin/env bash
set -euo pipefail

check_cmd() {
  local name="$1"
  if command -v "$name" >/dev/null 2>&1; then
    printf '%-18s ready    %s\n' "$name" "$(command -v "$name")"
  else
    printf '%-18s missing  -\n' "$name"
  fi
}

echo "Android Tool        Status   Location"
echo "-----------------------------------------------"
check_cmd adb
check_cmd emulator
check_cmd gradle
check_cmd java
check_cmd xcodebuild

printf '%-18s %s\n' "ANDROID_SDK_ROOT" "${ANDROID_SDK_ROOT:-missing}"
printf '%-18s %s\n' "ANDROID_HOME" "${ANDROID_HOME:-missing}"

