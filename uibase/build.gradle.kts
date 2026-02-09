/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("ncj-java-library-conventions") }

description = "UI elements that are independent of the CDM."

project.extra["project.title"] = "UI base library"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  implementation(ncjLibs.bounce)
  implementation(ncjLibs.findbugs.jsr305)
  implementation(ncjLibs.guava)
  implementation(ncjLibs.jfree.jcommon)
  implementation(ncjLibs.jfree.jfreechart)
  implementation(ncjLibs.jgoodies.forms)
  implementation(ncjLibs.jdom2)
  implementation(ncjLibs.lgooddatepicker)
  implementation(ncjLibs.protobuf)
  implementation(ncjLibs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-core"))
  testImplementation(project(":cdm-test-utils"))

  testImplementation(ncjLibs.commons.io)
  testImplementation(ncjLibs.google.truth)
  testImplementation(ncjLibs.mockito.core)

  testCompileOnly(ncjLibs.junit4)

  testRuntimeOnly(ncjLibs.junit5.platformLauncher)
  testRuntimeOnly(ncjLibs.junit5.vintageEngine)
  testRuntimeOnly(ncjLibs.logback.classic)
}

tasks.test {
  // Tell java to use ucar.util.prefs.PreferencesExtFactory to generate preference objects
  // Important for ucar.util.prefs.TestJavaUtilPreferences
  systemProperty("java.util.prefs.PreferencesFactory", "ucar.util.prefs.PreferencesExtFactory")
}
