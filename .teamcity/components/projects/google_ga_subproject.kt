package projects

import ProviderNameGa
import builds.AccTestConfiguration
import builds.readOnlySettings
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.RelativeId
import projects.reused.mmUpstream
import projects.reused.nightlyTests
import replaceCharsId
import vcs_roots.HashiCorpVCSRootGa
import vcs_roots.ModularMagicianVCSRootGa

// googleSubProjectGa returns a subproject that is used for testing terraform-provider-google (GA)
fun googleSubProjectGa(config: AccTestConfiguration): Project {

    var gaId = replaceCharsId("GOOGLE")

    return Project{
        id(gaId)
        name = "Google"
        description = "Subproject containing builds for testing the GA version of the Google provider"

        // Nightly Test project that uses hashicorp/terraform-provider-google
        subProject(nightlyTests(gaId, ProviderNameGa, HashiCorpVCSRootGa, config))

        // MM Upstream project that uses modular-magician/terraform-provider-google
        subProject(mmUpstream(gaId, ProviderNameGa, ModularMagicianVCSRootGa, config))

        params {
            readOnlySettings()
        }
    }
}