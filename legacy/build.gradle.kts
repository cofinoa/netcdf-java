/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("java-library-conventions") }

description = "Package that generates a jar file of legacy classes for backwards compatibility"

project.extra["project.title"] = "legacyJar"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(project(":cdm-core"))

  implementation(project(":grib"))
  implementation(project(":opendap"))
  implementation(project(":udunits"))

  implementation(libs.amazonaws.s3v1) // For CrawlableDatasetAmazonS3.
  implementation(libs.guava)
  implementation(libs.jdom2)
  implementation(libs.findbugs.jsr305)
  implementation(libs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))

  testCompileOnly(libs.junit4)

  testRuntimeOnly(libs.junit5.platformLauncher)
  testRuntimeOnly(libs.junit5.vintageEngine)
  testRuntimeOnly(libs.logback.classic)
}
