/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("platform-conventions") }

description =
  "BOM containing the public artifacts, and their third-party dependencies, that comprise the netCDF-Java Library."

extra["project.title"] = "netCDF-Java BOM with 3rd party libraries"

// allow references to other BOMs
javaPlatform.allowDependencies()

dependencies {
  api(platform(project(":netcdf-java-bom")))
  // covers libs.awssdk.apacheClient, libs.awssdk.s3, and libs.awssdk.sts
  api(platform(libs.awssdk.bom))
  // covers libs.jackson.core, libs.jackson.databind
  api(platform(libs.jackson.bom))

  constraints {
    api(libs.amazonaws.s3v1) // legacy subproject
    api(libs.beust.jcommander)
    api(libs.commons.math3)
    api(libs.findbugs.jsr305)
    api(libs.grpc.protobuf)
    api(libs.grpc.stub)
    api(libs.guava)
    api(libs.httpcomponents.httpclient)
    api(libs.httpcomponents.httpmime)
    api(libs.jdom2)
    api(libs.jj2000)
    api(libs.jna)
    api(libs.jodaTime)
    api(libs.protobuf)
    api(libs.re2j)
    api(libs.slf4j.api)
    api(libs.sensorweb.xmlGmlV321)
    api(libs.sensorweb.xmlOmV20)
    api(libs.sensorweb.xmlSamplingV20)
    api(libs.sensorweb.xmlSweCommonV20)
    api(libs.sensorweb.xmlWaterMLV20)
    api(libs.ssec.visad)
    api(libs.ssec.visadMcidasSlimUcarNs)
  }
}
