#!/usr/bin/env bash
set -euo pipefail

installer="/Users/haifengsong/.codex/skills/.system/skill-installer/scripts/install-skill-from-github.py"
codex_home="${CODEX_HOME:-$HOME/.codex}"
skills_dir="$codex_home/skills"

declare -a skill_paths=(
  "skills/.curated/doc"
  "skills/.curated/figma"
  "skills/.curated/figma-implement-design"
  "skills/.curated/playwright"
  "skills/.curated/screenshot"
  "skills/.curated/security-best-practices"
  "skills/.curated/security-threat-model"
)

if [[ ! -f "$installer" ]]; then
  echo "[error] skill installer not found: $installer"
  exit 1
fi

apply=false
if [[ "${1:-}" == "--apply" ]]; then
  apply=true
fi

pending_paths=()
for path in "${skill_paths[@]}"; do
  skill_name="$(basename "$path")"
  if [[ -d "$skills_dir/$skill_name" ]]; then
    echo "[skip] $skill_name already installed"
    continue
  fi
  pending_paths+=("$path")
done

if [[ "${#pending_paths[@]}" -eq 0 ]]; then
  echo "All recommended skills are already installed."
  exit 0
fi

cmd=(python3 "$installer" --repo openai/skills --path "${pending_paths[@]}")

if [[ "$apply" == true ]]; then
  printf '[run]'
  printf ' %q' "${cmd[@]}"
  printf '\n'
  "${cmd[@]}"
  echo "Restart Codex to pick up new skills."
else
  printf '[plan]'
  printf ' %q' "${cmd[@]}"
  printf '\n'
  echo "Dry run only. Re-run with --apply to install into ~/.codex/skills."
fi
