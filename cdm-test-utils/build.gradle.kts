/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("ncj-java-library-conventions") }

description =
  "A collection of reusable classes to be used internally for testing across the various THREDDS projects."

extra["project.title"] = "NetCDF-Java testing utilities"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(project(":cdm-core"))

  implementation(project(":httpservices"))

  implementation(ncjLibs.jdom2)
  implementation(ncjLibs.junit4)
  implementation(ncjLibs.re2j)
  implementation(ncjLibs.slf4j.api)
  implementation(ncjLibs.testcontainers)
}

tasks.withType<Jar>().configureEach {
  from(rootDir.absolutePath) {
    include("third-party-licenses/junit/")
    into("META-INF/")
  }
}
