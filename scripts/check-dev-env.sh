#!/usr/bin/env bash
set -euo pipefail

check_cmd() {
  local name="$1"
  if command -v "$name" >/dev/null 2>&1; then
    printf '%-16s ready    %s\n' "$name" "$(command -v "$name")"
  else
    printf '%-16s missing  -\n' "$name"
  fi
}

echo "Tool            Status   Location"
echo "-----------------------------------------------"
check_cmd codex
check_cmd python3
check_cmd java
check_cmd swift
check_cmd node
check_cmd npm
check_cmd gradle
check_cmd cjc
check_cmd cjpm
check_cmd keels
check_cmd xcodebuild
check_cmd adb

echo
echo "Specialized checks:"
echo "- bash ./.agents/setup/check-codex-ai-infra.sh"
echo "- bash ./.agents/setup/check-android-env.sh"
echo "- bash ./.agents/setup/check-kmp-env.sh"
echo "- bash ./.agents/setup/check-cjmp-env.sh"
echo "- bash ./scripts/kmp-doctor.sh"
