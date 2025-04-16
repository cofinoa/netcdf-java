/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE.txt for license information.
 */
package ucar.nc2.dataset;

import java.io.IOException;
import java.util.Objects;
import org.junit.Test;
import ucar.ma2.Array;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;
import ucar.nc2.constants.AxisType;
import ucar.unidata.util.test.TestDir;
import static com.google.common.truth.Truth.assertThat;

public class TestScaledCoordAxis1D {

  @Test
  public void testEnhancedOnce() throws IOException {
    String testfile = TestDir.cdmLocalTestDataDir + "scaledCoordAxis1D.nc4";
    try (NetcdfFile ncf = NetcdfFile.open(testfile, null); NetcdfDataset ncd = NetcdfDataset.openDataset(testfile)) {
      checkAxis(ncf, ncd, "x", AxisType.GeoX);
      checkAxis(ncf, ncd, "y", AxisType.GeoY);
    }
  }

  @Test
  public void testEnhancedOnceBuilders() throws IOException {
    String testfile = TestDir.cdmLocalTestDataDir + "scaledCoordAxis1D.nc4";
    try (NetcdfFile ncf = NetcdfFiles.open(testfile, null); NetcdfDataset ncd = NetcdfDatasets.openDataset(testfile)) {
      checkAxis(ncf, ncd, "x", AxisType.GeoX);
      checkAxis(ncf, ncd, "y", AxisType.GeoY);
    }
  }

  public void checkAxis(NetcdfFile ncf, NetcdfDataset ncd, String axisName, AxisType axisType) throws IOException {
    Variable var = ncf.findVariable(axisName);
    assertThat(var != null).isTrue();
    Array vals = var.read();
    Attribute scaleAttr = var.findAttribute("scale_factor");
    assertThat(scaleAttr).isNotNull();
    float scale = Objects.requireNonNull(scaleAttr.getNumericValue()).floatValue();
    Attribute offsetAttr = var.findAttribute("add_offset");
    assertThat(offsetAttr).isNotNull();
    float offset = Objects.requireNonNull(offsetAttr.getNumericValue()).floatValue();
    assertThat(vals.getShape().length).isEqualTo(1);
    int len = vals.getShape()[0];
    final float[] valsEnhanced = new float[len];
    for (int i = 0; i < len; i++) {
      valsEnhanced[i] = vals.getShort(i) * scale + offset;
    }
    // vals have a scale and offset.
    assertThat(vals).isNotNull();
    CoordinateAxis coodAxis = ncd.findCoordinateAxis(axisType);
    Array coordVals = coodAxis.read();
    for (int i = 0; i < len; i++) {
      assertThat(coordVals.getFloat(i)).isWithin(1e-6f).of(valsEnhanced[i]);
    }
  }
}
