package projects

import SharedResourceNameGa
import SharedResourceNamePr
import builds.AccTestConfiguration
import builds.readOnlySettings
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.sharedResource

// googleRootProject returns a root project that contains multiple subprojects for different use cases including:
// - Nightly tests
// - Running tests against the modular-magician fork
fun googleRootProject(config: AccTestConfiguration): Project {

    return Project{

        description = "A test project created by the refactored config code in https://github.com/hashicorp/terraform-provider-google/tree/teamcity-refactor"

        // Registry the VCS roots used by child projects on the root project
        vcsRoot(vcs_roots.HashiCorpVCSRoot)
        vcsRoot(vcs_roots.ModularMagicianVCSRoot)

        features {
            // For controlling sweeping of the GA nightly test project
            sharedResource {
                id = "GA_NIGHTLY_SERVICE_LOCK_SHARED_RESOURCE"
                name = SharedResourceNameGa
                enabled = true
                resourceType = customValues("foobar")
            }
            // TODO - control which of GA or Beta put here via MM
//            // For controlling sweeping of the Beta nightly test project
//            sharedResource {
//                id = "BETA_NIGHTLY_SERVICE_LOCK_SHARED_RESOURCE"
//                name = SharedResourceNameBeta
//                enabled = true
//                resourceType = customValues("foobar")
//            }
            // For controlling sweeping of the PR testing project
            sharedResource {
                id = "PR_SERVICE_LOCK_SHARED_RESOURCE"
                name = SharedResourceNamePr
                enabled = true
                resourceType = customValues("foobar")
            }
        }

        // Nightly Test project that uses hashicorp/terraform-provider-google(-beta)
        subProject(nightlyTests(vcs_roots.HashiCorpVCSRoot, config))

        // MM Upstream project that uses modular-magician/terraform-provider-google(-beta)
        subProject(mmUpstream(vcs_roots.ModularMagicianVCSRoot, config))

        params {
            readOnlySettings()
        }
    }
}
