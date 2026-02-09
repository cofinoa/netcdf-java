/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("ncj-platform-conventions") }

description =
  "Platform containing the test-only dependencies of the public artifacts that comprise the netCDF-Java Library. Published for use by the THREDDS Data Server."

extra["project.title"] = "netCDF-Java BOM test-only 3rd party libraries"

// allow references to other BOMs
javaPlatform.allowDependencies()

dependencies {
  // covers libs.junit5.vintageEngine, libs.junit5.platformLauncher
  api(platform(ncjLibs.junit5.bom))

  constraints {
    api((project(":cdm-test-utils")))

    api(ncjLibs.commons.compress)
    api(ncjLibs.commons.io)
    api(ncjLibs.google.truth)
    api(ncjLibs.grpc.testing)
    api(ncjLibs.jsoup)
    api(ncjLibs.junit4)
    api(ncjLibs.logback.classic)
    api(ncjLibs.mockito.core)
    api(ncjLibs.pragmatists.junitparams)
  }
}
