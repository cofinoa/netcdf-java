/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package ucar.nc2.ncml;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import org.junit.Test;
import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.NetcdfDatasets;
import ucar.nc2.time.Calendar;
import ucar.nc2.time.CalendarDate;
import ucar.nc2.time.CalendarDateFormatter;
import ucar.nc2.time.CalendarDateUnit;


public class TestAggScanNumericTime {

  @Test
  public void testNumericTime() throws IOException, InvalidRangeException, InterruptedException {
    String aggStringTime = "file:./" + TestNcmlRead.topDir + "aggExistingOne.xml";
    String aggNumericTime = "file:./" + TestNcmlRead.topDir + "aggExistingOneNumericTime.xml";

    try (NetcdfDataset ncStr = NetcdfDataset.openDataset(aggStringTime);
        NetcdfDataset ncNum = NetcdfDataset.openDataset(aggNumericTime)) {
      compareTimes(ncStr, ncNum);
    }
  }

  @Test
  public void testNumericTimeWithBuilders() throws IOException, InvalidRangeException, InterruptedException {
    String aggStringTime = "file:./" + TestNcmlRead.topDir + "aggExistingOne.xml";
    String aggNumericTime = "file:./" + TestNcmlRead.topDir + "aggExistingOneNumericTime.xml";

    try (NetcdfDataset ncStr = NetcdfDatasets.openDataset(aggStringTime);
        NetcdfDataset ncNum = NetcdfDatasets.openDataset(aggNumericTime)) {
      compareTimes(ncStr, ncNum);
    }
  }

  void compareTimes(NetcdfDataset ncStr, NetcdfDataset ncNum) throws IOException {
    Variable isoTimeVar = ncStr.findVariable("time");
    assertThat(isoTimeVar != null).isTrue();
    Variable numericTimeVar = ncNum.findVariable("time");
    assertThat(numericTimeVar != null).isTrue();
    Array isoTime = isoTimeVar.read();
    Array numericTime = numericTimeVar.read();
    Attribute numericTimeUnitAttr = numericTimeVar.findAttribute("units");
    assertThat(numericTimeUnitAttr).isNotNull();
    String numericTimeUnit = numericTimeUnitAttr.getStringValue();
    assertThat(numericTimeUnit).isNotEmpty();
    assertThat(numericTimeVar.getShape()).isEqualTo(isoTimeVar.getShape());
    CalendarDateUnit calDateUnit = CalendarDateUnit.of(Calendar.getDefault().name(), numericTimeUnit);
    for (int i = 0; i < numericTime.getSize(); i++) {
      CalendarDate calDateFromIso =
          CalendarDateFormatter.isoStringToCalendarDate(Calendar.getDefault(), isoTime.getObject(i).toString());
      CalendarDate calDateFromNum = calDateUnit.makeCalendarDate(numericTime.getInt(i));
      assertThat(calDateFromIso).isEqualTo(calDateFromNum);
    }
  }
}
