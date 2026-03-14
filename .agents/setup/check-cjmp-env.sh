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

echo "CJMP Tool           Status   Location"
echo "-----------------------------------------------"
check_cmd cjc
check_cmd cjpm
check_cmd keels
check_cmd cjfmt
check_cmd cjlint
check_cmd cjdb
check_cmd adb
check_cmd hdc
check_cmd xcodebuild

printf '%-18s %s\n' "CJMP_SDK_HOME" "${CJMP_SDK_HOME:-missing}"
printf '%-18s %s\n' "DEVECO_SDK_HOME" "${DEVECO_SDK_HOME:-missing}"
printf '%-18s %s\n' "CANGJIE_HOME" "${CANGJIE_HOME:-missing}"
