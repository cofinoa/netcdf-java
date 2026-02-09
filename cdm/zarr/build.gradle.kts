/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("ncj-java-library-conventions") }

description = "Reading Zarr files with the NetCDF-java library."

project.extra["project.title"] = "CDM Zarr support library"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(project(":cdm-core"))

  implementation(ncjLibs.findbugs.jsr305)
  implementation(ncjLibs.guava)
  implementation(ncjLibs.jackson.core)
  implementation(ncjLibs.jackson.databind)
  implementation(ncjLibs.slf4j.api)

  runtimeOnly(project(":libblosc2-jna"))

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-s3"))
  testImplementation(project(":cdm-test-utils"))

  testImplementation(ncjLibs.awssdk.s3) {
    // exclude netty nio client due to open CVEs. See
    // https://github.com/aws/aws-sdk-java-v2/issues/1632
    // we don't use the nio http client in our S3 related code,
    // so we should be ok here. Others may need to add it specifically
    // to their project if they are using our S3 stuff, but then it's
    // their explicit decision to run it.
    exclude(group = "software.amazon.awssdk", module = "netty-nio-client")
  }
  testImplementation(ncjLibs.google.truth)

  testCompileOnly(ncjLibs.junit4)

  testRuntimeOnly(project(":libblosc2-native"))

  testRuntimeOnly(ncjLibs.junit5.platformLauncher)
  testRuntimeOnly(ncjLibs.junit5.vintageEngine)
}
