/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("ncj-java-library-conventions") }

description = "Mcidas and Gempak IOSPs."

project.extra["project.title"] = "Mcidas and Gempak IOSPs"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(project(":cdm-core"))

  implementation(project(":grib"))
  implementation(project(":udunits"))

  implementation(ncjLibs.guava)
  implementation(ncjLibs.re2j)
  implementation(ncjLibs.slf4j.api)
  implementation(ncjLibs.ssec.visadMcidasSlimUcarNs)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))

  testImplementation(ncjLibs.google.truth)

  testCompileOnly(ncjLibs.junit4)

  testRuntimeOnly(ncjLibs.junit5.platformLauncher)
  testRuntimeOnly(ncjLibs.junit5.vintageEngine)
}
