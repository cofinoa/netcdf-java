/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("ncj-platform-conventions") }

description = "BOM containing the public artifacts that comprise the netCDF-Java Library."

extra["project.title"] = "netCDF-Java BOM"

dependencies {
  constraints {
    // list of public artifacts managed by build-logic/src/main/kotlin/ncj-base-conventions.gradle.kts
    val publicArtifacts = project.extra.get("public.artifacts")
    if (publicArtifacts is List<*>) {
      publicArtifacts.forEach { api(project(it.toString())) }
    } else {
      logger.error(
        "Cannot access the list of public artifacts. netcdf-java-bom will be incomplete!"
      )
    }
  }
}
