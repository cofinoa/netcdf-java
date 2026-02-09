/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("ncj-java-library-conventions") }

description = "Vis5D IOSP."

project.extra["project.title"] = "Vis5D IOSP"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  api(project(":cdm-core"))

  implementation(project(":cdm-mcidas"))

  implementation(ncjLibs.guava)
  implementation(ncjLibs.re2j)
  implementation(ncjLibs.slf4j.api)
  implementation(ncjLibs.ssec.visad)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-test-utils"))

  testCompileOnly(ncjLibs.junit4)

  testRuntimeOnly(ncjLibs.junit5.platformLauncher)
  testRuntimeOnly(ncjLibs.junit5.vintageEngine)
}
