plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
}

tasks.register("doctor") {
    group = "verification"
    description = "Print the intended KMP AI delivery loop for this repository."

    doLast {
        println("Telegram Compare KMP doctor")
        println("- Read framework-agnostic spec, acceptance report, and parity matrix first")
        println("- Implement in order: shared-domain -> shared-data -> androidApp -> iosApp")
        println("- Record each substantial AI run in framework-agnostic-assets/evaluation/ai-delivery-logs/")
        println("- Preferred commands:")
        println("  1. bash ../../scripts/kmp-doctor.sh")
        println("  2. ./gradlew :shared-domain:allTests :shared-data:allTests")
        println("  3. ./gradlew :androidApp:assembleDebug")
    }
}

tasks.register("printAiWorkflow") {
    group = "help"
    description = "Show where the KMP AI delivery runbooks live."

    doLast {
        println("KMP AI workflow docs:")
        println("- apps/kmp/docs/ai-workflow.md")
        println("- apps/kmp/docs/module-map.md")
        println("- apps/kmp/docs/debug-runbook.md")
        println("- .agents/prompts/kmp-delivery.md")
        println("- .agents/prompts/kmp-debug.md")
    }
}
