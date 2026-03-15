#!/usr/bin/env bash
set -euo pipefail

if ! command -v codex >/dev/null 2>&1; then
  echo "[error] codex CLI not found"
  exit 1
fi

mode="remote"
apply=false

for arg in "$@"; do
  case "$arg" in
    remote|desktop)
      mode="$arg"
      ;;
    --apply)
      apply=true
      ;;
    *)
      echo "[error] unknown argument: $arg"
      echo "usage: bash ./.agents/setup/bootstrap-figma-mcp.sh [remote|desktop] [--apply]"
      exit 1
      ;;
  esac
done

case "$mode" in
  remote)
    name="figma_remote"
    url="https://mcp.figma.com/mcp"
    note="Recommended when the project already has a Figma file and needs link-based design context."
    ;;
  desktop)
    name="figma_desktop"
    url="http://127.0.0.1:3845/mcp"
    note="Use only when Figma Desktop is running locally and Dev Mode access is available."
    ;;
esac

echo "[mode] $mode"
echo "[note] $note"

if codex mcp get "$name" >/dev/null 2>&1; then
  echo "[skip] $name already configured"
else
  if [[ "$apply" == true ]]; then
    echo "[add] $name -> $url"
    codex mcp add "$name" --url "$url"
  else
    echo "[plan] codex mcp add $name --url $url"
  fi
fi

if [[ "$apply" == false ]]; then
  echo
  echo "Dry run only. Re-run with --apply to write into ~/.codex/config.toml."
  exit 0
fi

echo
echo "Next:"
echo "1. Complete the OAuth / login flow if Codex prompts for it."
echo "2. Install Figma-related skills with:"
echo "   bash ./.agents/setup/install-curated-skills.sh --apply"
echo "3. Restart Codex so newly installed global skills are visible in the session."
