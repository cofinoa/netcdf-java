/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins {
  id("java-library-conventions")
  alias(libs.plugins.javacc)
}

description =
  "The ucar.units Java package is for decoding and encoding formatted unit specifications " +
    "(e.g. \"m/s\"), converting numeric values between compatible units (e.g. between \"m/s\" " +
    "and \"knot\"), and for performing arithmetic operations on units (e.g. dividing one unit " +
    "by another, or raising a unit to a power)."

extra["project.title"] = "UDUNITS"

extra["project.url"] = "https://www.unidata.ucar.edu/software/udunits/"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  implementation(libs.findbugs.jsr305)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(libs.slf4j.api)

  testCompileOnly(libs.junit4)

  testRuntimeOnly(libs.junit5.platformLauncher)
  testRuntimeOnly(libs.junit5.vintageEngine)
  testRuntimeOnly(libs.logback.classic)
}
