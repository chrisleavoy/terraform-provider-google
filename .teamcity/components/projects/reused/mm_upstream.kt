package projects.reused

import MMUpstreamProjectId
import ServiceSweeperName
import SharedResourceNamePr
import builds.*
import generated.PackagesList
import generated.ServicesList
import generated.SweepersList
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.RelativeId
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

fun mmUpstream(providerName: String, vcsRoot: GitVcsRoot, config: AccTestConfiguration): Project {

    // Shared resource allows ad hoc builds and sweeper builds to not clash
    var sharedResources: List<String> = listOf(SharedResourceNamePr)

    // Create build configs for each package defined in packages.kt and services.kt files
    val allPackages = PackagesList + ServicesList
    val packageBuildConfigs = BuildConfigurationsForPackages(allPackages, providerName, MMUpstreamProjectId, vcsRoot, sharedResources, config)

    // Create build config for sweeping the nightly test project - everything except projects
    val serviceSweeperConfig = BuildConfigurationForSweeper(providerName, ServiceSweeperName, SweepersList, MMUpstreamProjectId, vcsRoot, sharedResources, config)
    val trigger  = NightlyTriggerConfiguration()
    serviceSweeperConfig.addTrigger(trigger) // Only the sweeper is on a schedule in this project

    return Project {
        id(MMUpstreamProjectId)
        name = "MM Upstream Testing"
        description = "A project connected to the modular-magician/terraform-provider-${providerName} repository, to let users trigger ad-hoc builds against branches for PRs"

        // Register build configs in the project
        packageBuildConfigs.forEach { buildConfiguration: BuildType ->
            buildType(buildConfiguration)
        }
        buildType(serviceSweeperConfig)

        params{
            configureGoogleSpecificTestParameters(config)
        }
    }
}