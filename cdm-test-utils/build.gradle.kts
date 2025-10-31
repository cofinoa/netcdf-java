/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("java-library-conventions") }

description =
  "A collection of reusable classes to be used internally for testing across the various THREDDS projects."

extra["project.title"] = "NetCDF-Java testing utilities"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(project(":cdm-core"))

  implementation(project(":httpservices"))

  implementation(libs.jdom2)
  implementation(libs.junit4)
  implementation(libs.re2j)
  implementation(libs.slf4j.api)
  implementation(libs.testcontainers)
}

tasks.withType<Jar>().configureEach {
  from(rootDir.absolutePath) {
    include("third-party-licenses/junit/")
    into("META-INF/")
  }
}
