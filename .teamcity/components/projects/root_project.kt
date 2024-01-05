package projects

import builds.AccTestConfiguration
import builds.readOnlySettings
import jetbrains.buildServer.configs.kotlin.Project
import shared_resources.FoobarSharedResource

// googleRootProject returns a root project that contains multiple subprojects for different use cases including:
// - Nightly tests
// - Running tests against the modular-magician fork
fun googleRootProject(config: AccTestConfiguration): Project {

    return Project{

        description = "A test project created by the refactored config code in https://github.com/hashicorp/terraform-provider-google/tree/teamcity-refactor"

        // Registry the VCS roots used by child projects on the root proejct
        vcsRoot(vcs_roots.HashiCorpVCSRoot)
        vcsRoot(vcs_roots.ModularMagicianVCSRoot)

        features {
            FoobarSharedResource
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
