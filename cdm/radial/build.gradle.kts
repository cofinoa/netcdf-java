/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("ncj-java-library-conventions") }

description = "The Common Data Model (CDM) radial data IOSPs."

project.extra["project.title"] = "CDM radial library"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(project(":cdm-core"))

  implementation(project(":udunits"))

  implementation(ncjLibs.findbugs.jsr305)
  implementation(ncjLibs.guava)
  implementation(ncjLibs.jdom2)
  implementation(ncjLibs.re2j)
  implementation(ncjLibs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))
  testImplementation(project(":netcdf4"))

  testImplementation(ncjLibs.commons.io)
  testImplementation(ncjLibs.google.truth)
  testImplementation(ncjLibs.mockito.core)

  testCompileOnly(ncjLibs.junit4)

  testRuntimeOnly(ncjLibs.junit5.platformLauncher)
  testRuntimeOnly(ncjLibs.junit5.vintageEngine)
}
