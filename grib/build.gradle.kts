/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins {
  id("java-library-conventions")
  id("protobuf-conventions")
}

description = "Decoder for the GRIB 1 and 2 formats."

project.extra["project.title"] = "GRIB 1 and 2 IOSPs and Feature Collections"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(project(":cdm-core"))

  implementation(project(":libaec-jna"))

  implementation(libs.beust.jcommander)
  implementation(libs.findbugs.jsr305)
  implementation(libs.guava)
  implementation(libs.jdom2)
  implementation(libs.jj2000)
  implementation(libs.jna)
  implementation(libs.protobuf)
  implementation(libs.re2j)
  implementation(libs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))
  testImplementation(project(":udunits"))

  testImplementation(libs.google.truth)
  testImplementation(libs.jsoup)

  testCompileOnly(libs.junit4)

  testRuntimeOnly(project(":libaec-native"))

  testRuntimeOnly(libs.junit5.platformLauncher)
  testRuntimeOnly(libs.junit5.vintageEngine)
  testRuntimeOnly(libs.logback.classic)
}
