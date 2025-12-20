/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("java-library-conventions") }

description = "Reading Zarr files with the NetCDF-java library."

project.extra["project.title"] = "CDM Zarr support library"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(project(":cdm-core"))

  implementation(libs.findbugs.jsr305)
  implementation(libs.guava)
  implementation(libs.jackson.core)
  implementation(libs.jackson.databind)
  implementation(libs.slf4j.api)

  runtimeOnly(project(":libblosc2-jna"))

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-s3"))
  testImplementation(project(":cdm-test-utils"))

  testImplementation(libs.awssdk.s3) {
    // exclude netty nio client due to open CVEs. See
    // https://github.com/aws/aws-sdk-java-v2/issues/1632
    // we don't use the nio http client in our S3 related code,
    // so we should be ok here. Others may need to add it specifically
    // to their project if they are using our S3 stuff, but then it's
    // their explicit decision to run it.
    exclude(group = "software.amazon.awssdk", module = "netty-nio-client")
  }
  testImplementation(libs.google.truth)

  testCompileOnly(libs.junit4)

  testRuntimeOnly(project(":libblosc2-native"))

  testRuntimeOnly(libs.junit5.platformLauncher)
  testRuntimeOnly(libs.junit5.vintageEngine)
}
