/*
 * Copyright (c) HashiCorp, Inc.
 * SPDX-License-Identifier: MPL-2.0
 */

// this file is auto-generated with mmv1, any changes made here will be overwritten

import projects.googleCloudRootProject
import builds.AccTestConfiguration
import jetbrains.buildServer.configs.kotlin.*

version = "2023.11"

// The code below pulls context parameters from the TeamCity project.
// Context parameters aren't stored in VCS, and are managed manually.
// Due to this, the code needs to explicitly pull in values via the DSL and pass the values into other code.
// For DslContext docs, see https://teamcity.jetbrains.com/app/dsl-documentation/root/dsl-context/index.html

// PROVIDER / TEST CONFIGURATION
// Used to set ENVs needed for acceptance tests within the build configurations.
var billingAccount = DslContext.getParameter("billingAccount", "")
var billingAccount2 = DslContext.getParameter("billingAccount2", "")
var credentials = DslContext.getParameter("credentials", "")
var custId = DslContext.getParameter("custId", "")
var firestoreProject = DslContext.getParameter("firestoreProject", "")
var identityUser = DslContext.getParameter("identityUser", "")
var masterBillingAccount = DslContext.getParameter("masterBillingAccount", "")
var org = DslContext.getParameter("org", "")
var org2 = DslContext.getParameter("org2", "")
var orgDomain = DslContext.getParameter("orgDomain", "")
var project = DslContext.getParameter("project", "")
var projectNumber = DslContext.getParameter("projectNumber", "")
var region = DslContext.getParameter("region", "")
var serviceAccount = DslContext.getParameter("serviceAccount", "")
var zone = DslContext.getParameter("zone", "")

// Create AccTestConfiguration object that contains the above values to be set as environment variables on builds
// Separate configs are made for:
// - Use of the GA nightly tests project
// - Use of the GA PR tests project
// - Use of the Beta nightly tests project
// - Use of the Beta PR tests project

// TODO - refactor how we handle context parameters and convert to config for the 4 cases above
var configuration = AccTestConfiguration(
    billingAccount,
    billingAccount2,
    credentials,
    custId,
    firestoreProject,
    identityUser,
    masterBillingAccount,
    org,
    org2,
    orgDomain,
    project,
    projectNumber,
    region,
    serviceAccount,
    zone
)

// This is the entry point of the code in .teamcity/
// See https://teamcity.jetbrains.com/app/dsl-documentation/root/project.html
project(googleCloudRootProject(configuration))
