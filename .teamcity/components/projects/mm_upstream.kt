package projects

import ProviderName
import builds.AccTestConfiguration
import builds.BuildConfigurationsForPackages
import builds.configureGoogleSpecificTestParameters
import generated.PackagesList
import generated.ServicesList
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

const val MMUpstreamProjectId = "MMUpstreamTests"

fun mmUpstream(vcsRoot: GitVcsRoot, config: AccTestConfiguration): Project {

    // Create build configs for each package defined in packages.kt and services.kt files
    val allPackages = PackagesList + ServicesList
    val packageBuildConfigs = BuildConfigurationsForPackages(allPackages, ProviderName, MMUpstreamProjectId, vcsRoot, "FOOBAR_SHARED_RESOURCE", config)

    return Project {
        id(MMUpstreamProjectId)
        name = "MM Upstream Testing"
        description = "A project connected to the modular-magician/terraform-provider-${ProviderName} repository, to let users trigger ad-hoc builds against branches for PRs"

        // Register build configs in the project
        packageBuildConfigs.forEach { buildConfiguration: BuildType ->
            buildType(buildConfiguration)
        }

        params{
            configureGoogleSpecificTestParameters(config)
        }
    }
}