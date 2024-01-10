package builds

import generated.GetServiceNameList
import generated.ServicesList
import jetbrains.buildServer.configs.kotlin.BuildFeatures
import jetbrains.buildServer.configs.kotlin.SharedResources
import jetbrains.buildServer.configs.kotlin.buildFeatures.GolangFeature

// NOTE: this file includes Extensions of the Kotlin DSL class BuildFeature
// This allows us to reuse code in the config easily, while ensuring the same build features can be used across builds.
// See the class's documentation: https://teamcity.jetbrains.com/app/dsl-documentation/root/build-feature/index.html


const val UseTeamCityGoTest = false

fun BuildFeatures.golang() {
    if (UseTeamCityGoTest) {
        feature(GolangFeature {
            testFormat = "json"
        })
    }
}

// lockAllPackageValues takes a Shared Resource name as an argument and then registers locks on all service values inside that lock
fun SharedResources.lockAllPackageValues(sharedResource: String) {
    GetServiceNameList().forEach { serviceName ->
        lockSpecificValue(sharedResource, serviceName)
    }
}