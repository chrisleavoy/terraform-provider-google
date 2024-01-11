package projects

import SharedResourceNameBeta
import SharedResourceNameGa
import SharedResourceNamePr
import builds.AccTestConfiguration
import builds.AllContextParameters
import builds.readOnlySettings
import generated.GetPackageNameList
import generated.GetServiceNameList
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.sharedResource

// googleCloudRootProject returns a root project that contains a subprojects for the GA and Beta version of the
// Google provider. There are also resources to help manage the test projects used for acceptance tests.
fun googleCloudRootProject(allConfig: AllContextParameters): Project {

    return Project{

        description = "A test project created by the refactored config code in https://github.com/hashicorp/terraform-provider-google/tree/teamcity-refactor"

        // Registering the VCS roots used by subprojects
        vcsRoot(vcs_roots.HashiCorpVCSRootGa)
        vcsRoot(vcs_roots.HashiCorpVCSRootBeta)
        vcsRoot(vcs_roots.ModularMagicianVCSRootGa)
        vcsRoot(vcs_roots.ModularMagicianVCSRootBeta)

        features {
            // For controlling sweeping of the GA nightly test project
            sharedResource {
                id = "GA_NIGHTLY_SERVICE_LOCK_SHARED_RESOURCE"
                name = SharedResourceNameGa
                enabled = true
                resourceType = customValues(GetServiceNameList() + GetPackageNameList())
            }
            // For controlling sweeping of the Beta nightly test project
            sharedResource {
                id = "BETA_NIGHTLY_SERVICE_LOCK_SHARED_RESOURCE"
                name = SharedResourceNameBeta
                enabled = true
                resourceType = customValues(GetServiceNameList() + GetPackageNameList())
            }
            // For controlling sweeping of the PR testing project
            sharedResource {
                id = "PR_SERVICE_LOCK_SHARED_RESOURCE"
                name = SharedResourceNamePr
                enabled = true
                resourceType = customValues(GetServiceNameList() + GetPackageNameList())
            }
        }

        subProject(googleSubProjectGa(allConfig))
        subProject(googleSubProjectBeta(allConfig))
        subProject(projectSweeperSubProject(allConfig))

        params {
            readOnlySettings()
        }
    }
}
