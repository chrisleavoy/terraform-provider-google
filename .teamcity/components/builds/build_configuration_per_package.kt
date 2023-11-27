package builds

import DefaultBuildTimeoutDuration
import DefaultParallelism
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

fun BuildConfigurationsForPackages(packages: Map<String, Map<String, String>>, providerName: String, vcsRoot: GitVcsRoot, environmentVariables: AccTestConfiguration): List<BuildType> {
    val list = ArrayList<BuildType>()

    // Create build configurations for all packages, except sweeper
    packages.forEach { (packageName, info) ->
        val path: String = info.getValue("path").toString()
        val name: String = info.getValue("name").toString()
        val displayName: String = info.getValue("displayName").toString()

        val pkg = PackageDetails(packageName, displayName, providerName)
        val buildConfig = pkg.buildConfiguration(path, vcsRoot, DefaultParallelism, environmentVariables)
        list.add(buildConfig)
    }

    return list
}

class PackageDetails(private val packageName: String, private val displayName: String, private val providerName: String) {

    // buildConfiguration returns a BuildType for a service package
    // For BuildType docs, see https://teamcity.jetbrains.com/app/dsl-documentation/root/build-type/index.html
    fun buildConfiguration(path: String, vcsRoot: GitVcsRoot, parallelism: Int, environmentVariables: AccTestConfiguration, buildTimeout: Int = DefaultBuildTimeoutDuration): BuildType {

        val testPrefix = "TestAcc"
        val testTimeout = "12"
        val sweeperRegions = "" // Not used
        val sweeperRun = "" // Not used

        return BuildType {
            // TC needs a consistent ID for dynamically generated packages
            id(uniqueID())

            name = "%s - Acceptance Tests".format(displayName)

            vcs {
                root(vcsRoot)
                cleanCheckout = true
            }

            steps {
                setGitCommitBuildId()
                tagBuildToIndicatePurpose()
                configureGoEnv()
                downloadTerraformBinary()
                runAcceptanceTests()
            }

            features {
                golang()
            }

            params {
                configureGoogleSpecificTestParameters(environmentVariables)
                terraformAcceptanceTestParameters(parallelism, testPrefix, testTimeout)
                terraformSweeperParameters(sweeperRegions, sweeperRun)
                terraformLoggingParameters()
                terraformCoreBinaryTesting()
                terraformShouldPanicForSchemaErrors()
                readOnlySettings()
                workingDirectory(path)
            }

            artifactRules = "%teamcity.build.checkoutDir%/debug*.txt"

            failureConditions {
                errorMessage = true
                executionTimeoutMin = buildTimeout
            }

        }
    }

    private fun uniqueID(): String {
        // Replacing chars can be necessary, due to limitations on IDs
        // "ID should start with a latin letter and contain only latin letters, digits and underscores (at most 225 characters)."
        val pv = this.providerName.replace("-", "").uppercase()
        val pkg = this.packageName.replace("-", "").uppercase()

        return "%s_PACKAGE_%s".format(pv, pkg)
    }
}