/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package ucar.nc2.ft2.coverage.writer;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;
import ucar.nc2.constants.CF;
import ucar.nc2.constants.FeatureType;
import ucar.nc2.ft2.coverage.CoverageCollection;
import ucar.nc2.ft2.coverage.CoverageDatasetFactory;
import ucar.nc2.ft2.coverage.FeatureDatasetCoverage;
import ucar.nc2.write.NetcdfFileFormat;
import ucar.nc2.write.NetcdfFormatWriter;
import ucar.unidata.util.test.TestDir;
import ucar.unidata.util.test.category.NeedsCdmUnitTest;

public class TestCFGridCoverageWriter {

  @Rule
  public final TemporaryFolder tempFolder = new TemporaryFolder();

  @Test
  @Category(NeedsCdmUnitTest.class)
  public void testCFGridCoverageWriterNonKmProjectionParams() throws IOException, InvalidRangeException {
    String fileOut = tempFolder.newFile().getAbsolutePath();
    String fileIn = TestDir.cdmUnitTestDir + "ncss/test/falseEastingNorthingScaleReset.nc4";

    try (FeatureDatasetCoverage cc = CoverageDatasetFactory.open(fileIn)) {
      CoverageCollection gcs = cc.findCoverageDataset(FeatureType.GRID);
      assertNotNull(gcs);
      NetcdfFormatWriter.Builder writerb =
          NetcdfFormatWriter.builder().setNewFile(true).setFormat(NetcdfFileFormat.NETCDF3).setLocation(fileOut);

      CFGridCoverageWriter.Result result = CFGridCoverageWriter.write(gcs, null, null, false, writerb, 0);
      if (!result.wasWritten()) {
        throw new InvalidRangeException("Error writing: " + result.getErrorMessage());
      }
    }

    try (NetcdfFile ncf = NetcdfFiles.open(fileOut)) {
      Variable proj = ncf.findVariable("lambert_conformal_conic");
      assertNotNull(proj);
      Attribute feAttr = proj.findAttribute(CF.FALSE_EASTING);
      assertNotNull(feAttr);
      Attribute fnAttr = proj.findAttribute(CF.FALSE_NORTHING);
      assertNotNull(fnAttr);
      // GeoX axis in meters
      Variable x = ncf.findVariable("x");
      assertNotNull(x);
      assertThat(x.getUnitsString()).isEqualTo("m");
      // false_easting in m
      assertThat(feAttr.getNumericValue()).isEqualTo(400000.0);
      assertThat(fnAttr.getNumericValue()).isEqualTo(400000.0);
      // should not have a units attribute on the proj variable
      Attribute unitsAttr = proj.findAttribute(CF.UNITS);
      assertThat(unitsAttr).isNull();
    }
  }
}
