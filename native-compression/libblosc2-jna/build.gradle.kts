/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("ncj-java-library-conventions") }

description = "Java bindings for decoding C-Blosc2 compression using JNA."

extra["project.title"] = "C-Blosc2 compression decoder using JNA"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(ncjLibs.jna)

  implementation(project(":cdm-core"))

  implementation(ncjLibs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))

  testImplementation(ncjLibs.google.truth)

  testCompileOnly(ncjLibs.junit4)

  testRuntimeOnly(project(":libblosc2-native"))

  testRuntimeOnly(ncjLibs.junit5.platformLauncher)
  testRuntimeOnly(ncjLibs.junit5.vintageEngine)
  testRuntimeOnly(ncjLibs.logback.classic)
}
