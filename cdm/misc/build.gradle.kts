/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins {
  id("ncj-java-library-conventions")
  id("ncj-protobuf-conventions")
}

description = "The Common Data Model (CDM) misc IOSPs."

project.extra["project.title"] = "CDM misc IOSP library"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(project(":cdm-core"))

  implementation(project(":udunits"))

  implementation(ncjLibs.findbugs.jsr305)
  implementation(ncjLibs.guava)
  implementation(ncjLibs.protobuf)
  implementation(ncjLibs.re2j)
  implementation(ncjLibs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))
  testImplementation(project(":grib"))

  testImplementation(ncjLibs.commons.io)
  testImplementation(ncjLibs.google.truth)
  testImplementation(ncjLibs.mockito.core)

  testCompileOnly(ncjLibs.junit4)

  testRuntimeOnly(ncjLibs.junit5.platformLauncher)
  testRuntimeOnly(ncjLibs.junit5.vintageEngine)
}
