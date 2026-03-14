#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "$0")/.." && pwd)"

bash "$repo_root/.agents/setup/check-kmp-env.sh"
echo
bash "$repo_root/scripts/check-kmp-project.sh"
echo
echo "[KMP next commands]"
echo "cd $repo_root/apps/kmp"
echo "./gradlew doctor"
echo "./gradlew :shared-domain:allTests :shared-data:allTests"
echo "./gradlew :androidApp:assembleDebug"
echo "./gradlew :androidApp:assembleRelease"
