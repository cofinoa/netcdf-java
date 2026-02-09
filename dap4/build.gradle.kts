/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("ncj-java-library-conventions") }

description = "Data Access Protocol (DAP) version 4.0 client."

project.extra["project.title"] = "DAP4 Client"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  implementation(project(":cdm-core"))
  implementation(project(":httpservices"))

  implementation(ncjLibs.httpcomponents.httpclient)
  implementation(ncjLibs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))

  testImplementation(ncjLibs.google.truth)

  testCompileOnly(ncjLibs.junit4)

  testRuntimeOnly(ncjLibs.junit5.platformLauncher)
  testRuntimeOnly(ncjLibs.junit5.vintageEngine)
}
