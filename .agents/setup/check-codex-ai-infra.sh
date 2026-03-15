#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "$0")/../.." && pwd)"
codex_home="${CODEX_HOME:-$HOME/.codex}"
failed=0

print_row() {
  printf '%-30s %-10s %s\n' "$1" "$2" "$3"
}

check_path() {
  local label="$1"
  local path="$2"

  if [[ -e "$path" ]]; then
    print_row "$label" "ready" "$path"
  else
    print_row "$label" "missing" "$path"
    failed=1
  fi
}

check_optional_skill() {
  local skill="$1"
  local path="$codex_home/skills/$skill"

  if [[ -d "$path" ]]; then
    print_row "skill:$skill" "ready" "$path"
  else
    print_row "skill:$skill" "optional" "$path"
  fi
}

check_optional_mcp() {
  local server="$1"
  local hint="$2"

  if command -v codex >/dev/null 2>&1 && codex mcp get "$server" >/dev/null 2>&1; then
    print_row "mcp:$server" "ready" "~/.codex/config.toml"
  else
    print_row "mcp:$server" "optional" "$hint"
  fi
}

echo "[Codex repo-local]"
print_row "Item" "Status" "Evidence"
print_row "------------------------------" "----------" "------------------------------"
check_path "AGENTS" "$repo_root/AGENTS.md"
check_path "agents-readme" "$repo_root/.agents/README.md"
check_path "codex-mcp-bootstrap" "$repo_root/.agents/setup/bootstrap-codex-mcp.sh"
check_path "figma-mcp-bootstrap" "$repo_root/.agents/setup/bootstrap-figma-mcp.sh"
check_path "codex-skill-installer" "$repo_root/.agents/setup/install-curated-skills.sh"
check_path "codex-self-check" "$repo_root/.agents/setup/check-codex-ai-infra.sh"
check_path "ai-gap-list" "$repo_root/.agents/todos/ai-gap-list.md"

echo
echo "[Codex global]"
print_row "Item" "Status" "Evidence"
print_row "------------------------------" "----------" "------------------------------"

if command -v codex >/dev/null 2>&1; then
  print_row "codex" "ready" "$(command -v codex)"
else
  print_row "codex" "missing" "codex CLI not found in PATH"
  failed=1
fi

for server in openaiDeveloperDocs context7; do
  if command -v codex >/dev/null 2>&1 && codex mcp get "$server" >/dev/null 2>&1; then
    print_row "mcp:$server" "ready" "~/.codex/config.toml"
  else
    print_row "mcp:$server" "missing" "bash ./.agents/setup/bootstrap-codex-mcp.sh --apply"
    failed=1
  fi
done

check_optional_mcp "figma_remote" "bash ./.agents/setup/bootstrap-figma-mcp.sh remote --apply"
check_optional_mcp "figma_desktop" "bash ./.agents/setup/bootstrap-figma-mcp.sh desktop --apply"

for skill in doc playwright screenshot security-best-practices security-threat-model; do
  if [[ -d "$codex_home/skills/$skill" ]]; then
    print_row "skill:$skill" "ready" "$codex_home/skills/$skill"
  else
    print_row "skill:$skill" "missing" "bash ./.agents/setup/install-curated-skills.sh --apply"
    failed=1
  fi
done

check_optional_skill "figma"
check_optional_skill "figma-implement-design"

echo
if [[ "$failed" -eq 0 ]]; then
  echo "Result: required Codex AI infrastructure is ready for this repository."
else
  echo "Result: required Codex AI infrastructure has gaps. Review the missing rows above."
fi

exit "$failed"
