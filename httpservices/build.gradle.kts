/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("java-library-conventions") }

description = "HTTP Client Wrappers for the NetCDF-Java library."

extra["project.title"] = "HttpClient Wrappers"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(libs.guava)
  api(libs.httpcomponents.httpclient)

  implementation(libs.findbugs.jsr305)
  implementation(libs.httpcomponents.httpmime)
  implementation(libs.re2j)
  implementation(libs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))

  testImplementation(libs.google.truth)

  testCompileOnly(libs.junit4)

  testRuntimeOnly(libs.junit5.platformLauncher)
  testRuntimeOnly(libs.junit5.vintageEngine)
  testRuntimeOnly(libs.logback.classic)
}
