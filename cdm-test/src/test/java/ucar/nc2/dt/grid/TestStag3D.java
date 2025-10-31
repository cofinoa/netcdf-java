/*
 * Copyright (c) 2009-2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package ucar.nc2.dt.grid;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.nc2.dt.GridCoordSystem;
import ucar.ma2.Array;
import ucar.unidata.util.test.category.NeedsCdmUnitTest;
import ucar.unidata.util.test.TestDir;
import java.lang.invoke.MethodHandles;
import java.util.Formatter;

/**
 * Describe
 *
 * @author caron
 * @since Oct 16, 2009
 */
@Category(NeedsCdmUnitTest.class)
public class TestStag3D {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Test
  public void testSubset() throws Exception {
    ucar.nc2.dt.grid.GridDataset dataset = GridDataset.open(TestDir.cdmUnitTestDir + "ft/grid/stag/bora_feb.nc");

    GeoGrid grid = dataset.findGridByName("u");
    assert null != grid;
    System.out.printf("u shape= %s%n", showShape(grid.getShape()));

    GridCoordSystem gcs = grid.getCoordinateSystem();
    assert null != gcs;
    assert grid.getRank() == 4;

    ucar.unidata.geoloc.vertical.VerticalTransform vt = gcs.getVerticalTransform();
    Array a = vt.getCoordinateArray(0);
    System.out.printf("vt shape= %s%n", showShape(a.getShape()));

    dataset.close();
  }

  private String showShape(int[] shape) {
    Formatter f = new Formatter();
    for (int s : shape) {
      f.format(" %d", s);
    }
    return f.toString();
  }
}
