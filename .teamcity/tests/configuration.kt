/*
 * Copyright (c) HashiCorp, Inc.
 * SPDX-License-Identifier: MPL-2.0
 */

// this file is auto-generated with mmv1, any changes made here will be overwritten

package tests

import builds.UseTeamCityGoTest
import org.junit.Assert.assertTrue
import org.junit.Test
import projects.NightlyTestsProjectId
import projects.googleRootProject

class ConfigurationTests {
    @Test
    fun buildShouldFailOnError() {
        val project = googleRootProject(testConfiguration())
        project.buildTypes.forEach { bt ->
            assertTrue("Build '${bt.id}' should fail on errors!", bt.failureConditions.errorMessage)
        }
    }

    @Test
    fun buildShouldHaveGoTestFeature() {
        val project = googleRootProject(testConfiguration())
        project.buildTypes.forEach{ bt ->
            var exists = false
            bt.features.items.forEach { f ->
                if (f.type == "golang") {
                    exists = true
                }
            }

            if (UseTeamCityGoTest) {
                assertTrue("Build ${bt.name} doesn't have Go Test Json enabled", exists)
            }
        }
    }
}
