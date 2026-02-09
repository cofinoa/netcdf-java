/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("ncj-java-library-conventions") }

description = "Converts CDM DSGs to WaterML 2.0 timeseries and vice-versa."

project.extra["project.title"] = "NetCDF to WaterML Converter"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(project(":cdm-core"))

  implementation(ncjLibs.guava)
  implementation(ncjLibs.sensorweb.xmlGmlV321) { exclude(group = "stax", module = "stax-api") }
  implementation(ncjLibs.sensorweb.xmlOmV20) { exclude(group = "stax", module = "stax-api") }
  implementation(ncjLibs.sensorweb.xmlSamplingV20) { exclude(group = "stax", module = "stax-api") }
  implementation(ncjLibs.sensorweb.xmlSweCommonV20) { exclude(group = "stax", module = "stax-api") }
  implementation(ncjLibs.sensorweb.xmlWaterMLV20) { exclude(group = "stax", module = "stax-api") }
  implementation(ncjLibs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))

  testCompileOnly(ncjLibs.junit4)

  testRuntimeOnly(ncjLibs.junit5.platformLauncher)
  testRuntimeOnly(ncjLibs.junit5.vintageEngine)
  testRuntimeOnly(ncjLibs.logback.classic)
}

spotless {
  java {
    // exclude sources from erddap
    targetExclude("src/main/java/ucar/nc2/ogc/erddap/**/*.java")
  }
}

tasks.withType<Jar>().configureEach {
  from(rootDir.absolutePath) {
    include("third-party-licenses/erddap/", "third-party-licenses/NOAA_LICENSE")
    into("META-INF/")
  }
}
