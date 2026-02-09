/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("ncj-platform-conventions") }

description =
  "BOM containing the public artifacts, and their third-party dependencies, that comprise the netCDF-Java Library."

extra["project.title"] = "netCDF-Java BOM with 3rd party libraries"

// allow references to other BOMs
javaPlatform.allowDependencies()

dependencies {
  api(platform(project(":netcdf-java-bom")))
  // covers libs.awssdk.apacheClient, libs.awssdk.s3, and libs.awssdk.sts
  api(platform(ncjLibs.awssdk.bom))
  // covers libs.jackson.core, libs.jackson.databind
  api(platform(ncjLibs.jackson.bom))

  constraints {
    api(ncjLibs.amazonaws.s3v1) // legacy subproject
    api(ncjLibs.beust.jcommander)
    api(ncjLibs.commons.math3)
    api(ncjLibs.findbugs.jsr305)
    api(ncjLibs.grpc.protobuf)
    api(ncjLibs.grpc.stub)
    api(ncjLibs.guava)
    api(ncjLibs.httpcomponents.httpclient)
    api(ncjLibs.httpcomponents.httpmime)
    api(ncjLibs.jdom2)
    api(ncjLibs.jj2000)
    api(ncjLibs.jna)
    api(ncjLibs.jodaTime)
    api(ncjLibs.protobuf)
    api(ncjLibs.re2j)
    api(ncjLibs.slf4j.api)
    api(ncjLibs.sensorweb.xmlGmlV321)
    api(ncjLibs.sensorweb.xmlOmV20)
    api(ncjLibs.sensorweb.xmlSamplingV20)
    api(ncjLibs.sensorweb.xmlSweCommonV20)
    api(ncjLibs.sensorweb.xmlWaterMLV20)
    api(ncjLibs.ssec.visad)
    api(ncjLibs.ssec.visadMcidasSlimUcarNs)
  }
}
