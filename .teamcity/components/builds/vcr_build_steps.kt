package builds

import jetbrains.buildServer.configs.kotlin.BuildSteps
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep

fun BuildSteps.checkVcrEnvironmentVariables() {
    step(ScriptBuildStep {
        name = "Setup for running VCR tests: feedback about user-supplied environment variables"
        scriptContent = """
            #!/bin/bash
            echo "VCR TESTING ENVIRONMENT VARIABLE CHECKS"
            if [ "${'$'}VCR_MODE" = "" ]; then
                echo "VCR_MODE is not set"
                exit 1
            fi
            if [ "${'$'}VCR_PATH" = "" ]; then
                echo "VCR_PATH is not set"
                exit 1
            fi
            if [ "${'$'}GOOGLE_INFRA_PROJECT" = "" ]; then
                echo "GOOGLE_INFRA_PROJECT is not set"
                exit 1
            fi
            if [ "${'$'}TEST" = "" ]; then
                echo "TEST is not set - set it to a value like ./google/... or ./google-beta/services/compute"
                exit 1
            fi
            if [ "${'$'}TESTARGS" = "" ]; then
                echo "TESTARGS is not set - set it to a value like -run=TestAccFoobar"
                exit 1
            fi
        """.trimIndent()
        // ${'$'} is required to allow creating a script in TeamCity that contains
        // parts like ${GIT_HASH_SHORT} without having Kotlin syntax issues. For more info see:
        // https://youtrack.jetbrains.com/issue/KT-2425/Provide-a-way-for-escaping-the-dollar-sign-symbol-in-multiline-strings-and-string-templates
    })
}

fun BuildSteps.runVcrAcceptanceTests() {
    step(ScriptBuildStep {
        name = "Run Tests"
        scriptContent =  "go test \$(TEST) -v \$(TESTARGS) -timeout=\"%TIMEOUT%h\" -test.parallel=\"%PARALLELISM%\" -ldflags=\"-X=github.com/hashicorp/terraform-provider-google/version.ProviderVersion=acc\""
    })
}

fun BuildSteps.runVcrTestRecordingSetup() {
    step(ScriptBuildStep {
        name = "Setup for running VCR tests: if in REPLAY mode, download existing cassettes"
        scriptContent = """
            #!/bin/bash
            echo "VCR TESTING SETUP"
            echo "VCR_PATH: ${'$'}{VCR_PATH}"
            echo "VCR_MODE: ${'$'}{VCR_MODE}"
            if [ "${'$'}VCR_MODE" = "RECORDING" ]; then
                echo "Recording mode, skipping cassette retrieval"
                exit 0
            fi

            # REPLAY MODE - retrieve cassettes from GCS

            # Authenticate gcloud CLI
            echo "${'$'}{GOOGLE_CREDENTIALS}" > google-account.json
            gcloud auth activate-service-account --key-file=sa-key.json

            # Pull files from GCS
            gsutil ls -p ${'$'}GOOGLE_INFRA_PROJECT gs://ci-vcr-cassettes/fixtures/
            mkdir -p ${'$'}VCR_PATH
            gsutil -m cp gs://ci-vcr-cassettes/fixtures/* ${'$'}VCR_PATH
            # copy branch specific cassettes over master. This might fail but that's ok if the folder doesnt exist
            export BRANCH_NAME = %teamcity.build.branch%
            gsutil -m cp gs://ci-vcr-cassettes/${'$'}BRANCH_NAME/fixtures/* ${'$'}VCR_PATH
            ls ${'$'}VCR_PATH

            # Cleanup
            rm google-account.json
            gcloud auth application-default revoke
            gcloud auth revoke --all

            echo "Finished"
        """.trimIndent()
        // ${'$'} is required to allow creating a script in TeamCity that contains
        // parts like ${GIT_HASH_SHORT} without having Kotlin syntax issues. For more info see:
        // https://youtrack.jetbrains.com/issue/KT-2425/Provide-a-way-for-escaping-the-dollar-sign-symbol-in-multiline-strings-and-string-templates
    })
}

fun BuildSteps.runVcrTestRecordingSaveCassettes() {
    step(ScriptBuildStep {
        name = "Tasks after running VCR tests: if in RECORDING mode, push new cassettes to GCS"
        scriptContent = """
            #!/bin/bash
            echo "VCR TESTING POST"
            echo "VCR_PATH: ${'$'}{VCR_PATH}"
            echo "VCR_MODE: ${'$'}{VCR_MODE}"
            if [ "${'$'}VCR_MODE" = "REPLAYING" ]; then
            echo "Replaying mode, skipping"
            exit 0
            fi

            # RECORDING MODE - push new cassettes to GCS
            # Authenticate gcloud CLI
            echo "${'$'}{GOOGLE_CREDENTIALS}" > google-account.json
            gcloud auth activate-service-account --key-file=sa-key.json

            export BRANCH_NAME = %teamcity.build.branch%
            gsutil ls -p ${'$'}GOOGLE_INFRA_PROJECT gs://ci-vcr-cassettes/fixtures/
            if [ "${'$'}BRANCH_NAME" = "refs/heads/main" ]; then
                echo "Copying to main"
                gsutil -m cp ${'$'}VCR_PATH/* gs://ci-vcr-cassettes/fixtures/
            else
                echo "Copying to ${'$'}BRANCH_NAME"
                gsutil -m cp ${'$'}VCR_PATH/* gs://ci-vcr-cassettes/${'$'}BRANCH_NAME/fixtures/
            fi

            # Cleanup
            rm google-account.json
            gcloud auth application-default revoke
            gcloud auth revoke --all

            echo "Finished"
        """.trimIndent()
        // ${'$'} is required to allow creating a script in TeamCity that contains
        // parts like ${GIT_HASH_SHORT} without having Kotlin syntax issues. For more info see:
        // https://youtrack.jetbrains.com/issue/KT-2425/Provide-a-way-for-escaping-the-dollar-sign-symbol-in-multiline-strings-and-string-templates
    })
}