/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins {
  id("ncj-java-library-conventions")
  id("ncj-protobuf-conventions")
}

description = "Reading BUFR files with the NetCDF-java library."

extra["project.title"] = "BUFR IOSP"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(project(":cdm-core"))

  implementation(ncjLibs.beust.jcommander)
  implementation(ncjLibs.findbugs.jsr305)
  implementation(ncjLibs.guava)
  implementation(ncjLibs.jdom2)
  implementation(ncjLibs.protobuf)
  implementation(ncjLibs.re2j)
  implementation(ncjLibs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))

  testImplementation(ncjLibs.google.truth)

  testCompileOnly(ncjLibs.junit4)

  testRuntimeOnly(ncjLibs.junit5.platformLauncher)
  testRuntimeOnly(ncjLibs.junit5.vintageEngine)
}
