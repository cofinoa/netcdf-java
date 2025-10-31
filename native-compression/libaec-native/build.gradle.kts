/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

import java.net.URL
import java.security.DigestInputStream
import java.security.MessageDigest

plugins { id("java-library-conventions") }

description = "Jar distribution of native libraries for libaec compression."

project.extra["project.title"] = "Native libraries for libaec."

// zip file produced by GitHub workflow
val libaecNative = "libaec-native-1.1.3-fec016ecd4b8ff1918877e582898d4257c405168.zip"

// sha256 checksum from GitHub workflow output
val expectedChecksum = "3db1ba7bc95b48eff74501382b90b0c7d0770a98f369d8c376c8ca4b6003487e"

val resourceZip = file("$rootDir/project-files/native/libaec/$libaecNative")
val fetchNativeResources =
  tasks.register("fetchNativeResources") {
    outputs.file(resourceZip)
    doLast {
      if (!resourceZip.exists()) {
        logger.info("Fetching native libaec libraries.")
        var actualChecksum = ""
        val resourceUrl =
          "https://downloads.unidata.ucar.edu/netcdf-java/native/libaec/$libaecNative"
        URL(resourceUrl).openStream().use { ips ->
          val dips = DigestInputStream(ips, MessageDigest.getInstance("SHA-256"))
          resourceZip.outputStream().use { ops -> dips.copyTo(ops) }
          actualChecksum = dips.messageDigest.digest().toHexString()
        }
        if (actualChecksum != expectedChecksum) {
          throw RuntimeException(
            String.format(
              "Error: checksum on libaec.zip does not match expected value.\n" +
                "  Expected: %s\n  Actual: %s\n",
              expectedChecksum,
              actualChecksum,
            )
          )
        }
      }
    }
  }

val processNativeResources =
  tasks.register("processNativeResources", Copy::class) {
    inputs.file(resourceZip)
    from(zipTree(resourceZip))
    eachFile { relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray()) }
    destinationDir = layout.buildDirectory.dir("resources/main").get().asFile
    dependsOn(fetchNativeResources)
  }

tasks.processResources { dependsOn(processNativeResources) }
