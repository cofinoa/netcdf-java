/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

dependencyResolutionManagement {
  repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
  versionCatalogs { create("ncjLibs") { from(files("../gradle/ncj.libs.versions.toml")) } }
}

rootProject.name = "build-logic-ncj"
