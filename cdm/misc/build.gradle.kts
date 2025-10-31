/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins {
  id("java-library-conventions")
  id("protobuf-conventions")
}

description = "The Common Data Model (CDM) misc IOSPs."

project.extra["project.title"] = "CDM misc IOSP library"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(project(":cdm-core"))

  implementation(project(":udunits"))

  implementation(libs.findbugs.jsr305)
  implementation(libs.guava)
  implementation(libs.protobuf)
  implementation(libs.re2j)
  implementation(libs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))
  testImplementation(project(":grib"))

  testImplementation(libs.commons.io)
  testImplementation(libs.google.truth)
  testImplementation(libs.mockito.core)

  testCompileOnly(libs.junit4)

  testRuntimeOnly(libs.junit5.platformLauncher)
  testRuntimeOnly(libs.junit5.vintageEngine)
}
