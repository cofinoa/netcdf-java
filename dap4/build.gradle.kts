/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("java-library-conventions") }

description = "Data Access Protocol (DAP) version 4.0 client."

project.extra["project.title"] = "DAP4 Client"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  implementation(project(":cdm-core"))
  implementation(project(":httpservices"))

  implementation(libs.httpcomponents.api)
  implementation(libs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))

  testImplementation(libs.google.truth)

  testCompileOnly(libs.junit4)

  testRuntimeOnly(libs.junit5.platformLauncher)
  testRuntimeOnly(libs.junit5.vintageEngine)
}
