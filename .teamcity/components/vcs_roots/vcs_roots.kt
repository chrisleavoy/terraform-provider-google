package vcs_roots

import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot
import ProviderName

object HashiCorpVCSRoot: GitVcsRoot({
    name = "https://github.com/hashicorp/terraform-provider-${ProviderName}#refs/heads/main"
    url = "https://github.com/hashicorp/terraform-provider-${ProviderName}"
    branch = "refs/heads/main"
    branchSpec = "+:*"
})

object ModularMagicianVCSRoot: GitVcsRoot({
    name = "https://github.com/modular-magician/terraform-provider-${ProviderName}#refs/heads/main"
    url = "https://github.com/modular-magician/terraform-provider-${ProviderName}"
    branch = "refs/heads/main"
    branchSpec = "+:*"
})
