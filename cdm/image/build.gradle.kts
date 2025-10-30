/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("java-library-conventions") }

description = "A collection of utilities needed client-side, including IOSPs requiring java.awt."

project.extra["project.title"] = "Client-side CDM image library"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(project(":cdm-core"))

  implementation(libs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))

  testImplementation(libs.google.truth)

  testCompileOnly(libs.junit4)

  testRuntimeOnly(libs.junit5.platformLauncher)
  testRuntimeOnly(libs.junit5.vintageEngine)
}
