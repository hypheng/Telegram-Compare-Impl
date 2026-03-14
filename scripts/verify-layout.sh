#!/usr/bin/env bash
set -euo pipefail

declare -a paths=(
  "README.md"
  "AGENTS.md"
  ".agents/README.md"
  ".agents/research/industry-workflow.md"
  ".agents/research/ui-design-workflow.md"
  ".agents/todos/ai-gap-list.md"
  "framework-agnostic-spec/requirements/telegram-mvp.md"
  "framework-agnostic-spec/domain/domain-map.md"
  "framework-agnostic-spec/ux/core-flows.md"
  "framework-agnostic-spec/interface-design/README.md"
  "framework-agnostic-spec/comparison/evaluation-rubric.md"
  "framework-agnostic-spec/comparison/ai-delivery-observability.md"
  "framework-agnostic-spec/comparison/debugging-and-issue-workflow.md"
  "framework-agnostic-assets/evaluation/parity-matrix.md"
  "framework-agnostic-assets/evaluation/acceptance-reports/README.md"
  "framework-agnostic-assets/evaluation/acceptance-reports/TEMPLATE.md"
  "framework-agnostic-assets/evaluation/ai-delivery-logs/README.md"
  "framework-agnostic-assets/evaluation/ai-delivery-logs/TEMPLATE.md"
  "framework-agnostic-assets/evaluation/issue-taxonomy.md"
  "framework-agnostic-assets/design-evidence/README.md"
  "apps/kmp/README.md"
  "apps/cjmp/README.md"
  ".agents/skills/telegram-compare-product-design/SKILL.md"
  ".agents/skills/telegram-compare-ui-design/SKILL.md"
  ".agents/skills/telegram-compare-parity-delivery/SKILL.md"
  ".agents/skills/telegram-compare-ai-infra/SKILL.md"
  ".agents/skills/telegram-compare-kmp-delivery/SKILL.md"
  ".agents/skills/telegram-compare-cjmp-delivery/SKILL.md"
  ".github/ISSUE_TEMPLATE/config.yml"
  ".github/ISSUE_TEMPLATE/ai-common-friction.yml"
  ".github/ISSUE_TEMPLATE/ai-kmp-friction.yml"
  ".github/ISSUE_TEMPLATE/ai-cjmp-friction.yml"
)

failed=0

for path in "${paths[@]}"; do
  if [[ -e "$path" ]]; then
    echo "[ok] $path"
  else
    echo "[missing] $path"
    failed=1
  fi
done

exit "$failed"
