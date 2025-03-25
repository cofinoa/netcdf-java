/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package ucar.nc2.grib.grib2;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.util.Formatter;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import ucar.ma2.Array;
import ucar.ma2.MAMath;
import ucar.ma2.MAMath.MinMax;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;
import ucar.nc2.util.CompareNetcdf2;
import ucar.unidata.util.test.TestDir;
import ucar.unidata.util.test.category.NeedsCdmUnitTest;

public class TestDrs42 {

  @Test
  public void CCSDS1byte() throws IOException {
    String origFile = TestDir.localTestDataDir + "sref.pds2.grib2";
    // drs42File is origFile convert from Simple Packing to CCSDS by ecCodes
    // grib_set -w isGridded=1 -r -s packingType=grid_ccsds sref.pds2.grib2 sref.pds2.drs42.grib2
    // This file uses 8 bits per sample in the CCSDS configuration
    String drs42File = TestDir.localTestDataDir + "sref.pds2.drs42.grib2";
    final String variableName = "u-component_of_wind_height_above_ground_weightedMean";

    // stats from ecCodes
    final float expectedMax = 13.4F;
    final float expectedMin = -9.9F;
    final float expectedAverage = 0.246189F;

    final int expectedLength = 23865;
    final float tol = 1e-6F;

    try (NetcdfFile nc42 = NetcdfFiles.open(drs42File)) {
      Variable v = nc42.findVariable(variableName);
      assertThat(v != null).isTrue();
      Array data = v.read();

      assertThat(data).isNotNull();
      assertThat(data.getSize()).isEqualTo(expectedLength);
      MinMax extremes = MAMath.getMinMax(data);
      assertThat(extremes.max).isWithin(tol).of(expectedMax);
      assertThat(extremes.min).isWithin(tol).of(expectedMin);
      assertThat(MAMath.sumDouble(data) / data.getSize()).isWithin(tol).of(expectedAverage);

      // compare repacked ccsds data with original data
      try (NetcdfFile ncOrig = NetcdfFiles.open(origFile)) {
        Formatter f = new Formatter();
        CompareNetcdf2 compare = new CompareNetcdf2(f, false, false, true);
        boolean ok = compare.compare(ncOrig, nc42, null);
        System.out.printf("%s %s%n", ok ? "OK" : "NOT OK", f);
        assertThat(ok).isTrue();
      }
    }
  }

  @Test
  @Category(NeedsCdmUnitTest.class)
  public void checkVariable2Bytes() throws IOException {
    // This file uses 16 bits per sample in the CCSDS configuration
    String drs42File = TestDir.cdmUnitTestDir + "formats/grib2/drs42/"
        + "icon-eu_europe_regular-lat-lon_single-level_2025031912_014_T_2M_CCSDS.grib2";
    final String variableName = "Temperature_height_above_ground";

    // stats from ecCodes
    final float expectedMax = 298.187F;
    final float expectedMin = 255.639F;
    final float expectedAverage = 278.009F;

    final int expectedLength = 904689;
    final float tol = 1e-3F;

    try (NetcdfFile nc42 = NetcdfFiles.open(drs42File)) {
      Variable v = nc42.findVariable(variableName);
      assertThat(v != null).isTrue();
      Array data = v.read();

      assertThat(data).isNotNull();
      assertThat(data.getSize()).isEqualTo(expectedLength);
      MinMax extremes = MAMath.getMinMax(data);
      assertThat(extremes.max).isWithin(tol).of(expectedMax);
      assertThat(extremes.min).isWithin(tol).of(expectedMin);
      assertThat(MAMath.sumDouble(data) / data.getSize()).isWithin(tol).of(expectedAverage);
    }
  }
}
