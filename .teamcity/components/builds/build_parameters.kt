package builds

import DefaultTerraformCoreVersion
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.ParameterDisplay
import jetbrains.buildServer.configs.kotlin.ParametrizedWithType

// this file is copied from mmv1, any changes made here will be overwritten

// NOTE: this file includes Extensions of the Kotlin DSL class ParametrizedWithType
// This allows us to reuse code in the config easily, while ensuring the same parameters are set across builds.
// See the class's documentation: https://teamcity.jetbrains.com/app/dsl-documentation/root/parametrized-with-type/index.html


// AccTestConfiguration is used to easily pass values set via Context Parameters into the Kotlin code's entrypoint.
class AccTestConfiguration(
    val billingAccount: String,
    val billingAccount2: String,
    val credentials: String,
    val custId: String,
    val firestoreProject: String,
    val identityUser: String,
    val masterBillingAccount: String,
    val org: String,
    val org2: String,
    val orgDomain: String,
    val project: String,
    val projectNumber: String,
    val region: String,
    val serviceAccount: String,
    val zone: String,
)

// ParametrizedWithType.configureGoogleSpecificTestParameters allows build configs to be created
// with the environment variables needed to configure the provider and/or configure test code.
fun ParametrizedWithType.configureGoogleSpecificTestParameters(config: AccTestConfiguration) {
    hiddenPasswordVariable("env.GOOGLE_BILLING_ACCOUNT", config.billingAccount, "The billing account associated with the first google organization")
    hiddenPasswordVariable("env.GOOGLE_BILLING_ACCOUNT_2", config.billingAccount2, "The billing account associated with the second google organization")
    hiddenPasswordVariable("env.GOOGLE_CUST_ID", config.custId, "The ID of the Google Identity Customer")
    hiddenPasswordVariable("env.GOOGLE_ORG", config.org, "The Google Organization Id")
    hiddenPasswordVariable("env.GOOGLE_ORG_2", config.org2, "The second Google Organization Id")
    hiddenPasswordVariable("env.GOOGLE_MASTER_BILLING_ACCOUNT", config.masterBillingAccount, "The master billing account")
    hiddenVariable("env.GOOGLE_PROJECT", config.project, "The google project for this build")
    hiddenVariable("env.GOOGLE_ORG_DOMAIN", config.orgDomain, "The org domain")
    hiddenVariable("env.GOOGLE_PROJECT_NUMBER", config.projectNumber, "The project number associated with the project")
    hiddenVariable("env.GOOGLE_REGION", config.region, "The google region to use")
    hiddenVariable("env.GOOGLE_SERVICE_ACCOUNT", config.serviceAccount, "The service account")
    hiddenVariable("env.GOOGLE_ZONE", config.zone, "The google zone to use")
    hiddenVariable("env.GOOGLE_FIRESTORE_PROJECT", config.firestoreProject, "The project to use for firestore")
    hiddenVariable("env.GOOGLE_IDENTITY_USER", config.identityUser, "The user for the identity platform")
    hiddenPasswordVariable("env.GOOGLE_CREDENTIALS", config.credentials, "The Google credentials for this test runner")
}

// ParametrizedWithType.terraformAcceptanceTestParameters sets environment variables and build parameters
// that affect how the acceptance tests run.
fun ParametrizedWithType.terraformAcceptanceTestParameters(parallelism: Int, prefix: String, timeout: String) {
    hiddenVariable("env.TF_ACC", "1", "Set to a value to run the Acceptance Tests")
    text("PARALLELISM", "%d".format(parallelism))
    text("TEST_PREFIX", prefix)
    text("TIMEOUT", timeout)
}

// ParametrizedWithType.terraformSweeperParameters sets build parameters that affect how sweepers are run
fun ParametrizedWithType.terraformSweeperParameters(sweeperRegions: String, sweepRun: String) {
    text("SWEEPER_REGIONS", sweeperRegions)
    text("SWEEP_RUN", sweepRun)
}

// ParametrizedWithType.terraformSkipProjectSweeper sets a build parameters to skip the sweeper for project resources
fun ParametrizedWithType.terraformSkipProjectSweeper() {
    text("SKIP_PROJECT_SWEEPER", "1")
}

// ParametrizedWithType.terraformEnableProjectSweeper unsets a build parameter used to skip the sweeper for project resources
fun ParametrizedWithType.terraformEnableProjectSweeper() {
    text("SKIP_PROJECT_SWEEPER", "")
}

// BuildType.enableProjectSweep enables sweeping project resources after a build configuration has been initialised
fun BuildType.enableProjectSweep(){
    params {
        terraformEnableProjectSweeper()
    }
}

// ParametrizedWithType.terraformLoggingParameters sets environment variables and build parameters that
// affect which logs are shown and allows them to be saved
fun ParametrizedWithType.terraformLoggingParameters(providerName: String) {
    // Set logging levels to match old projects
    text("env.TF_LOG", "DEBUG")
    text("env.TF_LOG_CORE", "WARN")
    text("env.TF_LOG_SDK_FRAMEWORK", "INFO")

    // Set where logs are sent
    text("PROVIDER_NAME", providerName)
    text("env.TF_LOG_PATH_MASK", "%system.teamcity.build.checkoutDir%/debug-%PROVIDER_NAME%-%env.BUILD_NUMBER%-%s.txt") // .txt extension used to make artifacts open in browser, instead of download
}

fun ParametrizedWithType.readOnlySettings() {
    hiddenVariable("teamcity.ui.settings.readOnly", "true", "Requires build configurations be edited via Kotlin")
}

fun ParametrizedWithType.terraformCoreBinaryTesting() {
    text("env.TERRAFORM_CORE_VERSION", DefaultTerraformCoreVersion, "The version of Terraform Core which should be used for testing")
    hiddenVariable("env.TF_ACC_TERRAFORM_PATH", "%system.teamcity.build.checkoutDir%/tools/terraform", "The path where the Terraform Binary is located")
}

fun ParametrizedWithType.terraformShouldPanicForSchemaErrors() {
    hiddenVariable("env.TF_SCHEMA_PANIC_ON_ERROR", "1", "Panic if unknown/unmatched fields are set into the state")
}

fun ParametrizedWithType.workingDirectory(path: String) {
    text("PACKAGE_PATH", path, "", "The path at which to run - automatically updated", ParameterDisplay.HIDDEN)
}

fun ParametrizedWithType.hiddenVariable(name: String, value: String, description: String) {
    text(name, value, "", description, ParameterDisplay.HIDDEN)
}

fun ParametrizedWithType.hiddenPasswordVariable(name: String, value: String, description: String) {
    password(name, value, "", description, ParameterDisplay.HIDDEN)
}