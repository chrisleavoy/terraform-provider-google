package projects

import BuildConfigurationsForPackages
import jetbrains.buildServer.configs.kotlin.*
import PackagesList
import ProviderName
import ServicesList
import builds.AccTestConfiguration
import builds.configureGoogleSpecificTestParameters
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot


//val MMUpstreamProjectId = RelativeId("MMUpstream")

fun mmUpstream(vcsRoot: GitVcsRoot, config: AccTestConfiguration): Project {

    // Create build configs for each package defined in packages.kt and services.kt files
    val allPackages = PackagesList + ServicesList
    val packageBuildConfigs = BuildConfigurationsForPackages(allPackages, ProviderName, vcsRoot, config)

    return Project {
        id("MMUpstream")
        name = "MM Upstream Testing"
        description = "A project connected to the modular-magician/terraform-provider-${ProviderName} repository, to let users trigger ad-hoc builds against branches for PRs"

        // Register build configs in the project
        packageBuildConfigs.forEach { buildConfiguration ->
            buildType(buildConfiguration)
        }

        params{
            configureGoogleSpecificTestParameters(config)
        }
    }
}