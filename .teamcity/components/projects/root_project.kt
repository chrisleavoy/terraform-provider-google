package projects

import builds.AccTestConfiguration
import builds.readOnlySettings
import jetbrains.buildServer.configs.kotlin.Project

// googleRootProject returns a root project that contains multiple subprojects for different use cases including:
// - Nightly tests
// - Running tests against the modular-magician fork
fun googleRootProject(config: AccTestConfiguration): Project {

    return Project{

        description = "foobar"

        // Registry the VCS roots used by child projects on the root proejct
        vcsRoot(vcs_roots.HashiCorpVCSRoot)
        vcsRoot(vcs_roots.ModularMagicianVCSRoot)

        // Nightly Test project that uses hashicorp/terraform-provider-google(-beta)
        subProject(nightlyTests(vcs_roots.HashiCorpVCSRoot, config))

        // MM Upstream project that uses modular-magician/terraform-provider-google(-beta)
        subProject(mmUpstream(vcs_roots.ModularMagicianVCSRoot, config))

        params {
            readOnlySettings()
        }
    }
}
