/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE.txt for license information.
 */

package ucar.nc2.util.xml;

import static com.google.common.truth.Truth.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.Test;

public class TestRuntimeConfigParser {
  @Test
  public void testReflection() throws IOException {
    // not testing that the loading actually work, but testing that the reflection
    // calls in the RuntimeConfigParser work
    File nj22config =
        Paths.get("src/test/resources/runtimeConfig/nj22Config.xml").toAbsolutePath().normalize().toFile();
    StringBuilder errlog = new StringBuilder();
    try (FileInputStream fis = new FileInputStream(nj22config)) {
      RuntimeConfigParser.read(fis, errlog);
    }
    String err = errlog.toString();
    assertThat(err).isNotEmpty();
    // since this test lives in the netcdf4 subproject, we should only see
    // the "is not on classpath" message if the reflection call used in the
    // RuntimeConfigParser code fails
    assertThat(err).doesNotContain("is not on classpath");
  }
}
