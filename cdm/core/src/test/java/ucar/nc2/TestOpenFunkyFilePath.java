/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package ucar.nc2;

import static com.google.common.truth.Truth.assertThat;

import java.io.File;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ucar.nc2.util.IO;
import ucar.unidata.util.test.TestDir;

public class TestOpenFunkyFilePath {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Test
  public void testFileWithHash() throws IOException {
    File org = new File(TestDir.cdmLocalTestDataDir + "jan.nc");
    NetcdfFile ncf = NetcdfFiles.open(org.getAbsolutePath());
    assertThat(ncf).isNotNull();
    ncf.close();

    // create file to trigger error from https://github.com/Unidata/netcdf-java/issues/1492
    File copyWithHashName = File.createTempFile("jan#", ".nc", tempFolder.getRoot());
    IO.copyFile(org, copyWithHashName);

    NetcdfFile ncf2 = NetcdfFiles.open(copyWithHashName.getAbsolutePath());
    assertThat(ncf2).isNotNull();
    ncf2.close();

    NetcdfFile ncf3 = NetcdfFiles.open("file:" + copyWithHashName.getAbsolutePath());
    assertThat(ncf3).isNotNull();
    ncf3.close();

    NetcdfFile ncf4 = NetcdfFiles.open("file://" + copyWithHashName.getAbsolutePath());
    assertThat(ncf4).isNotNull();
    ncf4.close();
  }

}
