/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("java-library-conventions") }

description = "UI elements that are independent of the CDM."

project.extra["project.title"] = "UI base library"

dependencies {
  implementation(platform(project(":netcdf-java-platform")))

  implementation(libs.bounce)
  implementation(libs.findbugs.jsr305)
  implementation(libs.guava)
  implementation(libs.jfree.jcommon)
  implementation(libs.jfree.jfreechart)
  implementation(libs.jgoodies.forms)
  implementation(libs.jdom2)
  implementation(libs.lgooddatepicker)
  implementation(libs.protobuf)
  implementation(libs.slf4j.api)

  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":cdm-core"))
  testImplementation(project(":cdm-test-utils"))

  testImplementation(libs.commons.io)
  testImplementation(libs.google.truth)
  testImplementation(libs.mockito.core)

  testCompileOnly(libs.junit4)

  testRuntimeOnly(libs.junit5.platformLauncher)
  testRuntimeOnly(libs.junit5.vintageEngine)
  testRuntimeOnly(libs.logback.classic)
}

tasks.test {
  // Tell java to use ucar.util.prefs.PreferencesExtFactory to generate preference objects
  // Important for ucar.util.prefs.TestJavaUtilPreferences
  systemProperty("java.util.prefs.PreferencesFactory", "ucar.util.prefs.PreferencesExtFactory")
}
