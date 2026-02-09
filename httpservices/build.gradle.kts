/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("ncj-java-library-conventions") }

description = "HTTP Client Wrappers for the NetCDF-Java library."

extra["project.title"] = "HttpClient Wrappers"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(ncjLibs.guava)
  api(ncjLibs.httpcomponents.httpclient)

  implementation(ncjLibs.findbugs.jsr305)
  implementation(ncjLibs.httpcomponents.httpmime)
  implementation(ncjLibs.re2j)
  implementation(ncjLibs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))

  testImplementation(ncjLibs.google.truth)

  testCompileOnly(ncjLibs.junit4)

  testRuntimeOnly(ncjLibs.junit5.platformLauncher)
  testRuntimeOnly(ncjLibs.junit5.vintageEngine)
  testRuntimeOnly(ncjLibs.logback.classic)
}
