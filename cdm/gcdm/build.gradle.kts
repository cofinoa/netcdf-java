/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

import com.github.psxpaul.task.JavaExecFork
import com.google.protobuf.gradle.id

plugins {
  id("ncj-java-library-conventions")
  application
  id("ncj-protobuf-conventions")
  alias(ncjLibs.plugins.execfork)
}

description = "gRPC client and server implementation of CDM Remote Procedure Calls (gCDM)."

project.extra["project.title"] = "CDM Remote Procedure Calls"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(project(":cdm-core"))

  implementation(ncjLibs.grpc.protobuf)
  implementation(ncjLibs.grpc.stub)
  implementation(ncjLibs.slf4j.api)

  compileOnly(ncjLibs.tomcat.annotationsApi)

  runtimeOnly(project(":bufr"))
  runtimeOnly(project(":grib"))

  runtimeOnly(ncjLibs.grpc.nettyShaded)
  runtimeOnly(ncjLibs.logback.classic)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))
  testImplementation(project(":netcdf4"))

  testImplementation(ncjLibs.commons.io)
  testImplementation(ncjLibs.google.truth)
  testImplementation(ncjLibs.grpc.testing)
  testImplementation(ncjLibs.mockito.core)

  testCompileOnly(ncjLibs.junit4)

  testRuntimeOnly(ncjLibs.junit5.platformLauncher)
  testRuntimeOnly(ncjLibs.junit5.vintageEngine)
}

val libCatalog = extensions.getByType(VersionCatalogsExtension::class.java).named("ncjLibs")

protobuf {
  protoc { artifact = libCatalog.findLibrary("protobuf-protoc").get().get().toString() }
  plugins {
    id("grpc") {
      artifact = libCatalog.findLibrary("grpc-protocGenGrpcJava").get().get().toString()
    }
  }
  generateProtoTasks {
    ofSourceSet("main").forEach {
      it.plugins {
        // Apply the "grpc" plugin whose spec is defined above, without options.
        id("grpc")
      }
    }
  }
}

application { mainClass = "ucar.gcdm.server.GcdmServer" }

val startDaemon =
  tasks.register<JavaExecFork>("startDaemon") {
    classpath = sourceSets.main.get().runtimeClasspath
    main = "ucar.gcdm.server.GcdmServer"
    // To attach the debugger to the gcdm server add to the jvmArgs
    // '-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005'
    jvmArgs = listOf("-Xmx512m", "-Djava.awt.headless=true")
    standardOutput = project.layout.buildDirectory.file("gcdm_logs/gcdm.log")
    errorOutput = project.layout.buildDirectory.file("gcdm_logs/gcdm-error.log")
    waitForPort = 16111
    waitForOutput = "Server started, listening on 16111"
    dependsOn(tasks.testClasses)
    doLast { stopAfter = tasks.named("test") }
  }

tasks.test { dependsOn(startDaemon) }
