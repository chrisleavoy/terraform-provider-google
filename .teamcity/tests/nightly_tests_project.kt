/*
 * Copyright (c) HashiCorp, Inc.
 * SPDX-License-Identifier: MPL-2.0
 */

// this file is auto-generated with mmv1, any changes made here will be overwritten

package tests

//import ProjectSweeperName
import org.junit.Assert.assertTrue
import org.junit.Test
import NightlyTestsProjectId
import projects.googleCloudRootProject

class NightlyTestProjectsTests {
    @Test
    fun allBuildsShouldHaveTrigger() {
        val project = googleCloudRootProject(testConfiguration())
        project.subProjects.forEach{ subProject ->
            if (subProject.id.toString() == NightlyTestsProjectId ){
                subProject.buildTypes.forEach{ bt ->

                    assertTrue("Build configuration `${bt.name}` contains at least one trigger", bt.triggers.items.isNotEmpty())

                    // Look for at least one CRON trigger
                    var found: Boolean = false
                    for (item in bt.triggers.items){
                        if (item.type == "schedulingTrigger") {
                            found = true
                            break
                        }
                    }
                    assertTrue("Build configuration `${bt.name}` contains a CRON trigger", found)
                }
            }
        }
    }

//    @Test
//    fun containProjectSweeperOnlyIfGA() {
//        val rootProject = googleCloudRootProject(testConfiguration())
//        val gaProject = //TODO
//        val betaProject = //TODO
//
//            project.subProjects.forEach{ subProject ->
//            // Nightly Test Project should contain project sweeper IF it's testing the GA provider
//            if (subProject.id.toString() == NightlyTestsProjectId ){
//                subProject.buildTypes.forEach{ bt ->
//                    if (bt.name == ProjectSweeperName){
//                        if (ProviderName == "google"){
//                            assertTrue("The GA Nightly Test project should contain a Project Sweeper", true)
//                            return
//                        }
//                        if (ProviderName == "google-beta"){
//                            assertTrue("The Beta Nightly Test project should NOT contain a Project Sweeper", false)
//                            return
//                        }
//                    }
//                }
//            }
//        }
//
//        // If we haven't found a project sweeper in the code above
//        if (ProviderName == "google") {
//            assertTrue("The GA Nightly Test project should contain a Project Sweeper", false)
//        }
//    }
}
