/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

plugins {
  id("java-base-conventions")
  alias(libs.plugins.spotless)
}

description = "The Unidata netCDF-Java library (aka CDM)."

// To upgrade gradle, update the version and expected checksum values below
// and run ./gradlew wrapper twice
tasks.wrapper {
  distributionType = Wrapper.DistributionType.ALL
  gradleVersion = "9.2.0"
  distributionSha256Sum = "16f2b95838c1ddcf7242b1c39e7bbbb43c842f1f1a1a0dc4959b6d4d68abcac3"
}

spotless {
  // check all gradle build scripts (build-logic has its own formatting check)
  kotlinGradle {
    target("*.gradle.kts", "**/*.gradle.kts")
    targetExclude("build-logic/**/*")
    ktfmt().googleStyle()
  }
}

// Aggregate task for building all public artifacts
// Used to assemble the jars that should be scanned by OWASP Dependency Check
// job on Jenkins
tasks.register("buildPublicArtifacts") {
  group = "build"
  val publicArtifacts = project.extra.get("public.artifacts")
  if (publicArtifacts is List<*>) {
    dependsOn(publicArtifacts.map { ":$it:jar" })
  } else {
    logger.error(
      "Cannot access the list of public artifacts. The project-wide code coverage report will be incomplete!"
    )
  }
  dependsOn(":uber-jars:buildNetcdfAll", ":uber-jars:buildToolsUI", ":uber-jars:buildNcIdv")
}
