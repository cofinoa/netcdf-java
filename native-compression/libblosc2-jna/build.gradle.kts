/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("java-library-conventions") }

description = "Java bindings for decoding C-Blosc2 compression using JNA."

extra["project.title"] = "C-Blosc2 compression decoder using JNA"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(libs.jna)

  implementation(project(":cdm-core"))

  implementation(libs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))

  testImplementation(libs.google.truth)

  testCompileOnly(libs.junit4)

  testRuntimeOnly(project(":libblosc2-native"))

  testRuntimeOnly(libs.junit5.platformLauncher)
  testRuntimeOnly(libs.junit5.vintageEngine)
  testRuntimeOnly(libs.logback.classic)
}
