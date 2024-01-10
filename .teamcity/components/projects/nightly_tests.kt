package projects

import ProviderName
import SharedResourceNameBeta
import SharedResourceNameGa
import SharedResourceNamePr
import builds.*
import generated.PackagesList
import generated.ServicesList
import generated.SweepersList
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot


const val NightlyTestsProjectId = "NightlyTests"

fun nightlyTests(vcsRoot: GitVcsRoot, config: AccTestConfiguration): Project {

    // TODO Do this in MM templating
    var sharedResources: List<String> = listOf(SharedResourceNameGa)
    var allSharedResources: List<String> = listOf(SharedResourceNameGa, SharedResourceNameBeta, SharedResourceNamePr)

    // Create build configs to run acceptance tests for each package defined in packages.kt and services.kt files
    val allPackages = PackagesList + ServicesList
    val packageBuildConfigs = BuildConfigurationsForPackages(allPackages, ProviderName, NightlyTestsProjectId, vcsRoot, sharedResources, config)

    // Create build config for sweeping the nightly test project - everything except projects
    val serviceSweeperConfig = BuildConfigurationForSweeper("Service Sweeper", SweepersList, vcsRoot, sharedResources, config)
    // TODO this needs to be conditionally templated in based on GA/Beta
    val projectSweeperConfig = BuildConfigurationForSweeper("Project Sweeper", SweepersList, vcsRoot, allSharedResources, config)

    // Add CRON trigger to all build configurations
    val trigger  = NightlyTriggerConfiguration()
    packageBuildConfigs.forEach { buildConfiguration ->
        buildConfiguration.addTrigger(trigger)
    }
    serviceSweeperConfig.addTrigger(trigger)
    projectSweeperConfig.addTrigger(trigger)

    return Project {
        id(NightlyTestsProjectId)
        name = "Nightly Tests"
        description = "A project connected to the hashicorp/terraform-provider-${ProviderName} repository, where scheduled nightly tests run and users can trigger ad-hoc builds"

        // Register build configs in the project
        packageBuildConfigs.forEach { buildConfiguration ->
            buildType(buildConfiguration)
        }
        buildType(serviceSweeperConfig)
        buildType(projectSweeperConfig)

        params{
            configureGoogleSpecificTestParameters(config)
        }
    }
}