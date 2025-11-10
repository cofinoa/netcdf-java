package ucar.nc2.grib.grib2;

import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ucar.nc2.grib.coord.TimeCoordIntvDateValue;
import ucar.nc2.grib.grib2.table.Grib2Tables;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class TestPds8 {
  @Before
  public void openTestFile() throws IOException {
    String testfile = "../grib/src/test/data/index/example_pds_8_quirks.grib2.gbx9";

    Grib2Index gi = new Grib2Index();
    boolean success = gi.readIndex(testfile, -1);
    assertTrue(success);
    assertEquals(gi.getRecords().size(), 2);
    quirky = gi.getRecords().get(0);
    normal = gi.getRecords().get(1);
  }

  @Test
  public void testForQuirkyRecord() {
    Grib2Pds normal_pds = normal.getPDS();
    Grib2Pds quirky_pds = quirky.getPDS();

    assertEquals(normal_pds.getRawLength(), 58);
    assertEquals(quirky_pds.getRawLength(), 58);

    // byte 41 is numberOfTimeRange, which is generally 1 for
    // this type of GRIB2 record. For the quirk, it is zero.
    assertEquals(normal_pds.getOctet(42), 1);
    assertEquals(quirky_pds.getOctet(42), 0);
  }

  @Test
  public void testPds8ReadBasic() {
    Grib2Pds pds8 = quirky.getPDS();
    assertEquals(pds8.getTemplateNumber(), 8);
    assertEquals(pds8.getParameterCategory(), 1);
    assertEquals(pds8.getParameterNumber(), 29);
    assertEquals(pds8.getGenProcessType(), 2);
    assertEquals(pds8.getGenProcessId(), 105);
    assertEquals(pds8.getBackProcessId(), 0);
    assertEquals(pds8.getTimeUnit(), 1);
    assertEquals(pds8.getForecastTime(), 0);
  }

  @Test
  public void testPds8CanGetNormalTime() {
    Grib2Tables tbl = Grib2Tables.factory(normal);
    TimeCoordIntvDateValue tc = tbl.getForecastTimeInterval(normal);
    // start and end times are the same
    assertEquals(tc.getStart().getMillis(), tc.getEnd().getMillis());
    assertEquals(tc.getStart().getMillis(), 1500508800000L);
  }

  @Test
  public void testPds8CanStillGetQuirkyTime() {
    Grib2Tables tbl = Grib2Tables.factory(quirky);
    TimeCoordIntvDateValue tc = tbl.getForecastTimeInterval(quirky);
    // start and end times are the same
    assertEquals(tc.getStart().getMillis(), tc.getEnd().getMillis());
    assertEquals(tc.getStart().getMillis(), 1500508800000L);
  }

  /*
   * ====================== SECTION_4 ( length=58, padding=0 ) ======================
   * 1-4 section4Length = 58
   * 5 numberOfSection = 4
   * 6-7 NV = 0
   * 8-9 productDefinitionTemplateNumber = 8 [Average, accumulation, extreme values or other statistically processed
   * values at a horizontal level or in a horizontal layer in a continuous or non-continuous time interval
   * (grib2/tables/2/4.0.table) ]
   * 10 parameterCategory = 1 [Moisture (grib2/tables/2/4.1.0.table) ]
   * 11 parameterNumber = 8 [Total precipitation (kg m-2) (grib2/tables/2/4.2.0.1.table) ]
   * 12 typeOfGeneratingProcess = 2 [Forecast (grib2/tables/2/4.3.table) ]
   * 13 backgroundProcess = 0
   * 14 generatingProcessIdentifier = 105
   * 15-16 hoursAfterDataCutoff = 0
   * 17 minutesAfterDataCutoff = 0
   * 18 indicatorOfUnitOfTimeRange = 1 [Hour (grib2/tables/2/4.4.table) ]
   * 19-22 forecastTime = 0
   * 23 typeOfFirstFixedSurface = 1 [Ground or water surface (grib2/tables/2/4.5.table ,
   * grib2/tables/local/kwbc/1/4.5.table) ]
   * 24 scaleFactorOfFirstFixedSurface = 0
   * 25-28 scaledValueOfFirstFixedSurface = 0
   * 29 typeOfSecondFixedSurface = 255 [Missing (grib2/tables/2/4.5.table , grib2/tables/local/kwbc/1/4.5.table) ]
   * 30 scaleFactorOfSecondFixedSurface = 0
   * 31-34 scaledValueOfSecondFixedSurface = 0
   * 35-36 yearOfEndOfOverallTimeInterval = 2017
   * 37 monthOfEndOfOverallTimeInterval = 7
   * 38 dayOfEndOfOverallTimeInterval = 20
   * 39 hourOfEndOfOverallTimeInterval = 0
   * 40 minuteOfEndOfOverallTimeInterval = 0
   * 41 secondOfEndOfOverallTimeInterval = 0
   * 42 numberOfTimeRange = 1
   * 43-46 numberOfMissingInStatisticalProcess = 0
   * 47 typeOfStatisticalProcessing = 1 [Accumulation (grib2/tables/2/4.10.table) ]
   * 48 typeOfTimeIncrement = 2 [Successive times processed have same start time of forecast, forecast time is
   * incremented (grib2/tables/2/4.11.table) ]
   * 49 indicatorOfUnitForTimeRange = 1 [Hour (grib2/tables/2/4.4.table) ]
   * 50-53 lengthOfTimeRange = 0
   * 54 indicatorOfUnitForTimeIncrement = 255 [Missing (grib2/tables/2/4.4.table) ]
   * 55-58 timeIncrement = 0
   */
  private Grib2Record quirky;

  /*
   * ====================== SECTION_4 ( length=58, padding=0 ) ======================
   * 1-4 section4Length = 58
   * 5 numberOfSection = 4
   * 6-7 NV = 0
   * 8-9 productDefinitionTemplateNumber = 8 [Average, accumulation, extreme values or other statistically processed
   * values at a horizontal level or in a horizontal layer in a continuous or non-continuous time interval
   * (grib2/tables/2/4.0.table) ]
   * 10 parameterCategory = 1 [Moisture (grib2/tables/2/4.1.0.table) ]
   * 11 parameterNumber = 8 [Total precipitation (kg m-2) (grib2/tables/2/4.2.0.1.table) ]
   * 12 typeOfGeneratingProcess = 2 [Forecast (grib2/tables/2/4.3.table) ]
   * 13 backgroundProcess = 0
   * 14 generatingProcessIdentifier = 105
   * 15-16 hoursAfterDataCutoff = 0
   * 17 minutesAfterDataCutoff = 0
   * 18 indicatorOfUnitOfTimeRange = 1 [Hour (grib2/tables/2/4.4.table) ]
   * 19-22 forecastTime = 0
   * 23 typeOfFirstFixedSurface = 1 [Ground or water surface (grib2/tables/2/4.5.table ,
   * grib2/tables/local/kwbc/1/4.5.table) ]
   * 24 scaleFactorOfFirstFixedSurface = 0
   * 25-28 scaledValueOfFirstFixedSurface = 0
   * 29 typeOfSecondFixedSurface = 255 [Missing (grib2/tables/2/4.5.table , grib2/tables/local/kwbc/1/4.5.table) ]
   * 30 scaleFactorOfSecondFixedSurface = 0
   * 31-34 scaledValueOfSecondFixedSurface = 0
   * 35-36 yearOfEndOfOverallTimeInterval = 2017
   * 37 monthOfEndOfOverallTimeInterval = 7
   * 38 dayOfEndOfOverallTimeInterval = 20
   * 39 hourOfEndOfOverallTimeInterval = 0
   * 40 minuteOfEndOfOverallTimeInterval = 0
   * 41 secondOfEndOfOverallTimeInterval = 0
   * 42 numberOfTimeRange = 1
   * 43-46 numberOfMissingInStatisticalProcess = 0
   * 47 typeOfStatisticalProcessing = 1 [Accumulation (grib2/tables/2/4.10.table) ]
   * 48 typeOfTimeIncrement = 2 [Successive times processed have same start time of forecast, forecast time is
   * incremented (grib2/tables/2/4.11.table) ]
   * 49 indicatorOfUnitForTimeRange = 1 [Hour (grib2/tables/2/4.4.table) ]
   * 50-53 lengthOfTimeRange = 0
   * 54 indicatorOfUnitForTimeIncrement = 255 [Missing (grib2/tables/2/4.4.table) ]
   * 55-58 timeIncrement = 0
   */
  private Grib2Record normal;
}

/*
 * # steps to reproduce test data
 *
 * baseurl='www.ncei.noaa.gov/oa/prod-model/rapid-refresh/access/historical/analysis'
 * curl -O "https://${baseurl}/201707/20170720/rap_130_20170720_0000_000.grb2"
 *
 * codes_split_file -1 rap_130_20170720_0000_000.grb2
 *
 * # quirky
 * grib_dump -O -p section_4 \
 * -w section4Length=58,numberOfTimeRange=0 \
 * rap_130_20170720_0000_000.grb2_199
 *
 * # normal
 * grib_dump -O -p section_4 \
 * -w section4Length=58,numberOfTimeRange=1 \
 * rap_130_20170720_0000_000.grb2_212
 *
 * cat \
 * rap_130_20170720_0000_000.grb2_199 \
 * rap_130_20170720_0000_000.grb2_212 \
 * >example_pds_8_quirks.grib2
 *
 * # index
 * java -jar netcdfAll-5.10.0-SNAPSHOT.jar \
 * example_pds_8_quirks.grib2
 *
 * # generates example_pds_8_quirks.grib2.gbx9
 */
