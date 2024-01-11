package projects

import ProjectSweeperName
import SharedResourceNameBeta
import SharedResourceNameGa
import SharedResourceNamePr
import builds.*
import generated.SweepersList
import jetbrains.buildServer.configs.kotlin.Project
import replaceCharsId
import vcs_roots.HashiCorpVCSRootGa

// projectSweeperSubProject returns a subproject that contains a sweeper for project resources
// Sweeping projects is an edge case because it doesn't respect boundaries between different testing projects GA/Beta/PR
fun projectSweeperSubProject(config: AccTestConfiguration): Project {

    val projectId = replaceCharsId("PROJECT_SWEEPER")

    // Get a list of ALL shared resources, to help the project sweeper avoid clashing with any other running build
    val sharedResources: List<String> = listOf(SharedResourceNameGa, SharedResourceNameBeta, SharedResourceNamePr)

    // Create build config for sweeping project resources
    val serviceSweeperConfig = BuildConfigurationForSweeper("N/A", ProjectSweeperName, SweepersList, projectId, HashiCorpVCSRootGa, sharedResources, config)
    serviceSweeperConfig.enableProjectSweep()
    val trigger  = NightlyTriggerConfiguration()
    serviceSweeperConfig.addTrigger(trigger)

    return Project{
        id(projectId)
        name = "Project Sweeper"
        description = "Project containing a build configuration for sweeping project resources"

        // Register build configs in the project
        buildType(serviceSweeperConfig)

        params {
            readOnlySettings()
        }
    }
}