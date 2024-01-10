package projects

import ProviderName
import SharedResourceNamePr
import builds.AccTestConfiguration
import builds.BuildConfigurationForSweeper
import builds.BuildConfigurationsForPackages
import builds.configureGoogleSpecificTestParameters
import generated.PackagesList
import generated.ServicesList
import generated.SweepersList
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

const val MMUpstreamProjectId = "MMUpstreamTests"

fun mmUpstream(vcsRoot: GitVcsRoot, config: AccTestConfiguration): Project {

    var sharedResources: List<String> = listOf(SharedResourceNamePr)

    // Create build configs for each package defined in packages.kt and services.kt files
    val allPackages = PackagesList + ServicesList
    val packageBuildConfigs = BuildConfigurationsForPackages(allPackages, ProviderName, MMUpstreamProjectId, vcsRoot, sharedResources, config)

    // Create build config for sweeping the nightly test project - everything except projects
    val serviceSweeperConfig = BuildConfigurationForSweeper("Service Sweeper", SweepersList, MMUpstreamProjectId, vcsRoot, sharedResources, config)

    return Project {
        id(MMUpstreamProjectId)
        name = "MM Upstream Testing"
        description = "A project connected to the modular-magician/terraform-provider-${ProviderName} repository, to let users trigger ad-hoc builds against branches for PRs"

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