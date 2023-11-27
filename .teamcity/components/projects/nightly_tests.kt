package projects

import jetbrains.buildServer.configs.kotlin.*
import ProviderName
import builds.AccTestConfiguration
import builds.BuildConfigurationsForPackages
import builds.configureGoogleSpecificTestParameters
import generated.PackagesList
import generated.ServicesList
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot


//val NightlyTestsProjectId = RelativeId("NightlyTests")

fun nightlyTests(vcsRoot: GitVcsRoot, config: AccTestConfiguration): Project {

    // Create build configs for each package defined in packages.kt and services.kt files
    val allPackages = PackagesList + ServicesList
    val packageBuildConfigs = BuildConfigurationsForPackages(allPackages, ProviderName, vcsRoot, config)

//    val postSweeperConfig = SweeperBuildConfiguration() // TODO

    return Project {
        id("NightlyTests")
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