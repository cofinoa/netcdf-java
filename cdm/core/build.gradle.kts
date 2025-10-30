/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins {
  id("java-library-conventions")
  id("protobuf-conventions")
}

description =
  "The Common Data Model (CDM) is a Java interface to NetCDF files, as well as to many other types of" +
    "scientific data formats."

extra["project.title"] = "CDM core library"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  implementation(project(":httpservices"))
  implementation(project(":udunits"))

  implementation(libs.beust.jcommander)
  implementation(libs.commons.math3)
  implementation(libs.findbugs.jsr305)
  implementation(libs.guava)
  implementation(libs.jdom2)
  implementation(libs.jodaTime)
  implementation(libs.protobuf)
  implementation(libs.re2j)
  implementation(libs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))

  testImplementation(libs.commons.io)
  testImplementation(libs.google.truth)
  testImplementation(libs.mockito.core)

  testCompileOnly(libs.junit4)

  testRuntimeOnly(libs.junit5.platformLauncher)
  testRuntimeOnly(libs.junit5.vintageEngine)
  testRuntimeOnly(libs.logback.classic)
}

tasks.withType<Jar>().configureEach {
  from(rootDir.absolutePath) {
    include("third-party-licenses/edal/")
    into("META-INF/")
  }
}

// todo: move to ucar.nc2.write.Ncdump in 6?
tasks.jar { manifest { attributes("Main-Class" to "ucar.nc2.NCdumpW") } }
