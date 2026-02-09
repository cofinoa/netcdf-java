/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins {
  id("ncj-java-library-conventions")
  id("ncj-protobuf-conventions")
}

description =
  "The Common Data Model (CDM) is a Java interface to NetCDF files, as well as to many other types of" +
    "scientific data formats."

extra["project.title"] = "CDM core library"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  implementation(project(":httpservices"))
  implementation(project(":udunits"))

  implementation(ncjLibs.beust.jcommander)
  implementation(ncjLibs.commons.math3)
  implementation(ncjLibs.findbugs.jsr305)
  implementation(ncjLibs.guava)
  implementation(ncjLibs.jdom2)
  implementation(ncjLibs.jodaTime)
  implementation(ncjLibs.protobuf)
  implementation(ncjLibs.re2j)
  implementation(ncjLibs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))

  testImplementation(ncjLibs.commons.io)
  testImplementation(ncjLibs.google.truth)
  testImplementation(ncjLibs.mockito.core)

  testCompileOnly(ncjLibs.junit4)

  testRuntimeOnly(ncjLibs.junit5.platformLauncher)
  testRuntimeOnly(ncjLibs.junit5.vintageEngine)
  testRuntimeOnly(ncjLibs.logback.classic)
}

tasks.withType<Jar>().configureEach {
  from(rootDir.absolutePath) {
    include("third-party-licenses/edal/")
    into("META-INF/")
  }
}

// todo: move to ucar.nc2.write.Ncdump in 6?
tasks.jar { manifest { attributes("Main-Class" to "ucar.nc2.NCdumpW") } }
