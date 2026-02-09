/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("ncj-java-library-conventions") }

description =
  "Open-source Project for a Network Data Access Protocol, modified " +
    "for NetCDF purpose. This artifact is a derivative work from the official " +
    "OPeNDAP library (https://www.opendap.org/), modified by UCAR. The " +
    "packages were renamed from \"dods.*\" to \"opendap.*\" and the groupID " +
    "from \"org.opendap\" to \"edu.ucar\"."

extra["project.title"] = "OPeNDAP (2.0) Client"

extra["project.vendor"] = "OPeNDAP"

extra["project.url"] = "https://www.opendap.org/"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(project(":cdm-core"))
  api(project(":httpservices"))

  implementation(ncjLibs.findbugs.jsr305)
  implementation(ncjLibs.guava)
  implementation(ncjLibs.jdom2)
  implementation(ncjLibs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))

  testImplementation(ncjLibs.google.truth)
  testImplementation(ncjLibs.pragmatists.junitparams)

  testCompileOnly(ncjLibs.junit4)

  testRuntimeOnly(ncjLibs.junit5.platformLauncher)
  testRuntimeOnly(ncjLibs.junit5.vintageEngine)
  testRuntimeOnly(ncjLibs.logback.classic)
}
