package projects

import ProviderName
import SharedResourceNameBeta
import SharedResourceNameGa
import SharedResourceNamePr
import builds.*
import generated.PackagesList
import generated.ServicesList
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.sequential
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot


const val NightlyTestsProjectId = "NightlyTests"

fun nightlyTests(vcsRoot: GitVcsRoot, config: AccTestConfiguration): Project {

    // TODO Do this in MM templating
    var sharedResources: List<String> = listOf(SharedResourceNameGa)
    var allSharedResources: List<String> = listOf(SharedResourceNameGa, SharedResourceNameBeta, SharedResourceNamePr)

    // Create build configs to run acceptance tests for each package defined in packages.kt and services.kt files
    val allPackages = PackagesList + ServicesList
    val packageBuildConfigs = BuildConfigurationsForPackages(allPackages, ProviderName, NightlyTestsProjectId, vcsRoot, sharedResources, config)

    // Add CRON trigger to all build configurations
    val trigger  = NightlyTriggerConfiguration()
    packageBuildConfigs.forEach { buildConfiguration ->
        buildConfiguration.addTrigger(trigger)
    }

    return Project {
        id(NightlyTestsProjectId)
        name = "Nightly Tests"
        description = "A project connected to the hashicorp/terraform-provider-${ProviderName} repository, where scheduled nightly tests run and users can trigger ad-hoc builds"

        // Register build configs in the project
        packageBuildConfigs.forEach { buildConfiguration ->
            buildType(buildConfiguration)
        }
        // buildType(postSweeperConfig) // TODO

        // Create a build chain so all acceptance test builds must finish before the post-sweeper runs.
        sequential {
            parallel{
                packageBuildConfigs.forEach { buildConfiguration ->
                    buildType(buildConfiguration)
                }
            }

            // buildType(postSweeperConfig) // TODO
        }

        params{
            configureGoogleSpecificTestParameters(config)
        }
    }
}