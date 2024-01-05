package shared_resources

import jetbrains.buildServer.configs.kotlin.SharedResource

object FoobarSharedResource : SharedResource({
    id = "FOOBAR_SHARED_RESOURCE"
    name = "Foobar"
    enabled = true
    resourceType = customValues("foobar")
})