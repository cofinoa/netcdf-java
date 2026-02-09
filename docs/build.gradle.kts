/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

import java.io.OutputStream

plugins {
  id("ncj-java-base-conventions")
  alias(ncjLibs.plugins.spotless)
}

description = "Generate the project documentation, including the various flavors of javadocs."

extra["project.title"] = "Project documentation"

dependencies {
  testImplementation(platform(project(":netcdf-java-testing-platform")))

  testImplementation(project(":bufr"))
  testImplementation(project(":cdm-core"))
  testImplementation(project(":cdm-s3"))
  testImplementation(project(":cdm-test-utils"))
  testImplementation(project(":grib"))
  testImplementation(project(":netcdf4"))
  testImplementation(project(":opendap"))
  testImplementation(project(":udunits"))

  testImplementation(ncjLibs.google.truth)
  testImplementation(ncjLibs.jdom2)
  testImplementation(ncjLibs.slf4j.api)

  testCompileOnly(ncjLibs.junit4)

  testRuntimeOnly(ncjLibs.junit5.platformLauncher)
  testRuntimeOnly(ncjLibs.junit5.vintageEngine)
  testRuntimeOnly(ncjLibs.logback.classic)
}

// This is the public interface. Future changes to the API will attempt to remain backwards
// compatible with it.
val publicApi =
  listOf(
    "thredds/client/catalog/*.java",
    "thredds/client/catalog/builder/*.java",
    "ucar/ma2/*.java",
    "ucar/nc2/*.java",
    "ucar/nc2/constants/*.java",
    "ucar/nc2/dataset/*.java",
    "ucar/nc2/dataset/spi/*.java",
    "ucar/nc2/iosp/*.java",
    "ucar/nc2/time/*.java",
    "ucar/nc2/units/*.java",
    "ucar/nc2/util/*.java",
    "ucar/nc2/write/*.java",
    "ucar/unidata/geoloc/*.java",
    "ucar/unidata/io/*.java",
    "ucar/unidata/io/spi/*.java",
  )

val buildJavadocPublicApi =
  tasks.register<Javadoc>("buildJavadocPublicApi") {
    description = "Generate Javadoc for the public API without deprecations."
    title = "NetCDF-Java CDM Public API v${version}"
    destinationDir = layout.buildDirectory.dir("javadoc").get().asFile

    options {
      showFromPublic()
      require(this is StandardJavadocDocletOptions)
      noDeprecated()
    }

    val cdmCoreSourceSet = rootProject.project("cdm-core").sourceSets.main.get()
    source = cdmCoreSourceSet.allJava
    classpath = files(arrayOf(cdmCoreSourceSet.compileClasspath, cdmCoreSourceSet.output))

    publicApi.forEach { include(it) }
  }

val buildJavadocPublicApiWithDeps =
  tasks.register<Javadoc>("buildJavadocPublicApiWithDeps") {
    description =
      "Generate Javadoc for the CDM subproject - included deprecated classes and methods."
    title = "NetCDF-Java CDM Public API v${version} - with deprecations"
    destinationDir = layout.buildDirectory.dir("javadoc-with-deprecations").get().asFile

    options { showFromPublic() }

    val cdmCoreSourceSet = rootProject.project("cdm-core").sourceSets.main.get()
    source = cdmCoreSourceSet.allJava
    classpath = files(arrayOf(cdmCoreSourceSet.compileClasspath, cdmCoreSourceSet.output))

    publicApi.forEach { include(it) }
  }

val buildJavadocAll =
  tasks.register<Javadoc>("buildJavadocAll") {
    description = "Generate Javadoc for all Java subprojects."

    title = "NetCDF-Java All API v${version}"
    destinationDir = layout.buildDirectory.dir("javadocAll").get().asFile
    // list of public artifacts managed by
    // build-logic/src/main/kotlin/ncj-base-conventions.gradle.kts
    val publicArtifacts = project.extra.get("public.artifacts")
    if (publicArtifacts is List<*>) {
      publicArtifacts.forEach {
        val subprojectSourceSet = rootProject.project("${it}").sourceSets.main.get()
        source = subprojectSourceSet.allJava
        classpath = files(arrayOf(subprojectSourceSet.compileClasspath, subprojectSourceSet.output))
      }
    } else {
      logger.error("Cannot access the list of public artifacts. JavadocAll will be incomplete!")
    }
  }

// Documentation build using Docker
val catalogs = extensions.getByType<VersionCatalogsExtension>()

val docTheme =
  "unidata-jekyll-docs:${catalogs.named("ncjLibs").findVersion("unidata-doc-theme").get().requiredVersion}"

val isGitHub = System.getenv("GITHUB_ACTIONS") != null
val imageBaseUrl = if (isGitHub) "ghcr.io/unidata" else "docker.io/unidata"
val dockerImage = "${imageBaseUrl}/${docTheme}"

val siteBuildDir = layout.buildDirectory.dir("site")

val buildJekyllSite =
  tasks.register<Exec>("buildJekyllSite") {
    group = "documentation"
    description = "Build the netCDF-Java documentation."
    val buildDocInputs = fileTree(".")
    buildDocInputs.exclude("build/", ".gradle", ".jekyll-cache")
    inputs.files(buildDocInputs)
    outputs.dir(siteBuildDir)
    commandLine(
      "docker",
      "run",
      "--rm",
      "-e",
      "SRC_DIR=/netcdf-java/docs/src/site",
      "-v",
      "$rootDir:/netcdf-java",
      "-v",
      "./${relativePath(siteBuildDir.get().toString())}:/site",
      dockerImage,
      "build",
    )
  }

tasks.register<Exec>("serveJekyllSite") {
  group = "documentation"
  description = "Start a local server to live edit the netCDF-Java documentation."
  commandLine("ls")
  commandLine(
    "docker",
    "run",
    "--rm",
    "-d",
    "--name",
    "netcdf-java-docs-server",
    "-e",
    "SRC_DIR=/netcdf-java/docs/src/site",
    "-v",
    "$rootDir:/netcdf-java",
    "-p",
    "4005:4005",
    dockerImage,
    "serve",
    "--livereload",
  )
  standardOutput = OutputStream.nullOutputStream()
  doLast {
    val msg = "NetCDF-Java documentation available at http://localhost:4005"
    val bannerBorder = String(CharArray(msg.length + 4) { '#' })
    println("\n${bannerBorder}\n# ${msg} #\n${bannerBorder}")
  }
}

tasks.register<Exec>("stopServe") {
  group = "documentation"
  description = "Stop the local server used while live editing the netCDF-Java documentation."
  commandLine("docker", "stop", "netcdf-java-docs-server")
  delete("$projectDir/src/site/Gemfile")
  delete("$projectDir/src/site/Gemfile.lock")
}

tasks.withType<JavaCompile>().configureEach {
  // show deprecation warnings
  // we have a github action that checks to make sure we are not
  // using deprecated code in the tutorial examples
  options.compilerArgs.add("-Xlint:deprecation")
}

spotless {
  java {
    target("src/test/java/**/*.java")
    eclipse()
      .configFile(
        "$rootDir/project-files/code-styles/eclipse-style-guide.xml",
        "src/test/style/style-override.properties",
      )
    encoding("UTF-8")
  }
}

tasks.build {
  dependsOn(buildJekyllSite, buildJavadocPublicApi, buildJavadocPublicApiWithDeps, buildJavadocAll)
}
