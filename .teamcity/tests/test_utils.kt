/*
 * Copyright (c) HashiCorp, Inc.
 * SPDX-License-Identifier: MPL-2.0
 */

// this file is copied from mmv1, any changes made here will be overwritten

package tests

import builds.AccTestConfiguration
import jetbrains.buildServer.configs.kotlin.AbsoluteId

fun testConfiguration(): AccTestConfiguration {
    return AccTestConfiguration("billingAccount", "billingAccount2", "credentials", "custId", "firestoreProject", "identityUser", "masterBillingAccount", "org", "org2", "orgDomain", "project", "projectNumber", "region", "serviceAccount", "zone")
}

fun testVcsRootId(): AbsoluteId {
    return AbsoluteId("TerraformProviderFoobar")
}