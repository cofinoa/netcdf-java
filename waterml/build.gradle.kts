/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("java-library-conventions") }

description = "Converts CDM DSGs to WaterML 2.0 timeseries and vice-versa."

project.extra["project.title"] = "NetCDF to WaterML Converter"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(project(":cdm-core"))

  implementation(libs.guava)
  implementation(libs.sensorweb.xmlGmlV321) { exclude(group = "stax", module = "stax-api") }
  implementation(libs.sensorweb.xmlOmV20) { exclude(group = "stax", module = "stax-api") }
  implementation(libs.sensorweb.xmlSamplingV20) { exclude(group = "stax", module = "stax-api") }
  implementation(libs.sensorweb.xmlSweCommonV20) { exclude(group = "stax", module = "stax-api") }
  implementation(libs.sensorweb.xmlWaterMLV20) { exclude(group = "stax", module = "stax-api") }
  implementation(libs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))

  testCompileOnly(libs.junit4)

  testRuntimeOnly(libs.junit5.platformLauncher)
  testRuntimeOnly(libs.junit5.vintageEngine)
  testRuntimeOnly(libs.logback.classic)
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
