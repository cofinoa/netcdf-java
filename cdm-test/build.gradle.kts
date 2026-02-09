import kotlin.collections.set

/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins { id("ncj-java-base-conventions") }

description =
  "Classes for CDM unit and integration testing. Relies on having access to " +
    "the cdmUnitTest directory"

project.extra["project.title"] = "Extended CDM Testing"

dependencies {
  testImplementation(platform(project(":netcdf-java-platform")))
  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":bufr"))
  testImplementation(project(":cdm-core"))
  testImplementation(project(":cdm-s3"))
  testImplementation(project(":cdm-test-utils"))
  testImplementation(project(":grib"))
  testImplementation(project(":netcdf4"))
  testImplementation(project(":udunits"))

  testImplementation(ncjLibs.awssdk.s3)
  testImplementation(ncjLibs.commons.compress)
  testImplementation(ncjLibs.commons.io)
  testImplementation(ncjLibs.findbugs.jsr305)
  testImplementation(ncjLibs.google.truth)
  testImplementation(ncjLibs.guava)
  testImplementation(ncjLibs.jdom2)
  testImplementation(ncjLibs.jj2000)
  testImplementation(ncjLibs.re2j)
  testImplementation(ncjLibs.slf4j.api)
  testCompileOnly(ncjLibs.junit4)

  testRuntimeOnly(ncjLibs.junit5.platformLauncher)
  testRuntimeOnly(ncjLibs.junit5.vintageEngine)
  testRuntimeOnly(ncjLibs.logback.classic)
}

val testVersions = project.extra["project.testLtsVersions"]

if (testVersions is List<*>) {
  testVersions.forEach {
    tasks.register<Test>(
      if (it == project.extra["project.minimumJdkVersion"]) "testIndexCreation"
      else "testIndexCreation${it}"
    ) {
      group = "Verification"
      testClassesDirs = sourceSets.test.get().output.classesDirs
      classpath = sourceSets.test.get().runtimeClasspath
      useJUnitPlatform()
      filter { includeTestsMatching("ucar.nc2.grib.TestGribIndexCreation") }
      javaLauncher.set(
        project.javaToolchains.launcherFor {
          languageVersion = JavaLanguageVersion.of(it.toString().toInt())
        }
      )
      dependsOn(tasks.classes, tasks.testClasses)
    }
  }
}
