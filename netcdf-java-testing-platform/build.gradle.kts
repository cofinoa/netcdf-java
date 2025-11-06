/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("platform-conventions") }

description =
  "Platform containing the test-only dependencies of the public artifacts that comprise the netCDF-Java Library. Published for use by the THREDDS Data Server."

extra["project.title"] = "netCDF-Java BOM test-only 3rd party libraries"

// allow references to other BOMs
javaPlatform.allowDependencies()

dependencies {
  // covers libs.junit5.vintageEngine, libs.junit5.platformLauncher
  api(platform(libs.junit5.bom))

  constraints {
    api((project(":cdm-test-utils")))

    api(libs.commons.compress)
    api(libs.commons.io)
    api(libs.google.truth)
    api(libs.grpc.testing)
    api(libs.jsoup)
    api(libs.junit4)
    api(libs.logback.classic)
    api(libs.mockito.core)
    api(libs.pragmatists.junitparams)
  }
}
