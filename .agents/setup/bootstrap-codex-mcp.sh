#!/usr/bin/env bash
set -euo pipefail

if ! command -v codex >/dev/null 2>&1; then
  echo "[error] codex CLI not found"
  exit 1
fi

declare -a servers=(
  "openaiDeveloperDocs https://developers.openai.com/mcp"
  "context7 https://mcp.context7.com/mcp"
)

apply=false
if [[ "${1:-}" == "--apply" ]]; then
  apply=true
fi

for entry in "${servers[@]}"; do
  name="${entry%% *}"
  url="${entry#* }"

  if codex mcp get "$name" >/dev/null 2>&1; then
    echo "[skip] $name already configured"
    continue
  fi

  if [[ "$apply" == true ]]; then
    echo "[add] $name -> $url"
    codex mcp add "$name" --url "$url"
  else
    echo "[plan] codex mcp add $name --url $url"
  fi
done

if [[ "$apply" == false ]]; then
  echo
  echo "Dry run only. Re-run with --apply to write into ~/.codex/config.toml."
fi

