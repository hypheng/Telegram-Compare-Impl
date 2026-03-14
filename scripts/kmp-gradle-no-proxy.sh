#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "$0")/.." && pwd)"

if [[ $# -eq 0 ]]; then
  echo "usage: ./scripts/kmp-gradle-no-proxy.sh <gradle-task> [more-gradle-args...]"
  exit 1
fi

cd "$repo_root/apps/kmp"

./gradlew \
  --no-daemon \
  -Djava.net.useSystemProxies=false \
  -DsocksProxyHost= \
  -DsocksProxyPort=0 \
  -Dhttp.proxyHost= \
  -Dhttp.proxyPort=0 \
  -Dhttps.proxyHost= \
  -Dhttps.proxyPort=0 \
  "$@"
