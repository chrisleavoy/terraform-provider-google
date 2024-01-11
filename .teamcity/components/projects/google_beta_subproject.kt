package projects

import ProviderNameBeta
import builds.AccTestConfiguration
import builds.readOnlySettings
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.RelativeId
import projects.reused.mmUpstream
import projects.reused.nightlyTests
import replaceCharsId
import vcs_roots.HashiCorpVCSRootBeta
import vcs_roots.ModularMagicianVCSRootBeta

// googleSubProjectBeta returns a subproject that is used for testing terraform-provider-google-beta (Beta)
fun googleSubProjectBeta(config: AccTestConfiguration): Project {

    var betaId = replaceCharsId("GOOGLE_BETA")

    return Project{
        id(betaId)
        name = "Google Beta"
        description = "Subproject containing builds for testing the Beta version of the Google provider"

        // Nightly Test project that uses hashicorp/terraform-provider-google-beta
        subProject(nightlyTests(betaId, ProviderNameBeta, HashiCorpVCSRootBeta, config))

        // MM Upstream project that uses modular-magician/terraform-provider-google-beta
        subProject(mmUpstream(betaId, ProviderNameBeta, ModularMagicianVCSRootBeta, config))

        params {
            readOnlySettings()
        }
    }
}