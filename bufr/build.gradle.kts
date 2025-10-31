/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins {
  id("java-library-conventions")
  id("protobuf-conventions")
}

description = "Reading BUFR files with the NetCDF-java library."

extra["project.title"] = "BUFR IOSP"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(project(":cdm-core"))

  implementation(libs.beust.jcommander)
  implementation(libs.findbugs.jsr305)
  implementation(libs.guava)
  implementation(libs.jdom2)
  implementation(libs.protobuf)
  implementation(libs.re2j)
  implementation(libs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))

  testImplementation(libs.google.truth)

  testCompileOnly(libs.junit4)

  testRuntimeOnly(libs.junit5.platformLauncher)
  testRuntimeOnly(libs.junit5.vintageEngine)
}
