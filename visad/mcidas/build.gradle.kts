/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("java-library-conventions") }

description = "Mcidas and Gempak IOSPs."

project.extra["project.title"] = "Mcidas and Gempak IOSPs"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(project(":cdm-core"))

  implementation(project(":grib"))
  implementation(project(":udunits"))

  implementation(libs.guava)
  implementation(libs.re2j)
  implementation(libs.slf4j.api)
  implementation(libs.ssec.visadMcidasSlimUcarNs)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))

  testImplementation(libs.google.truth)

  testCompileOnly(libs.junit4)

  testRuntimeOnly(libs.junit5.platformLauncher)
  testRuntimeOnly(libs.junit5.vintageEngine)
}
