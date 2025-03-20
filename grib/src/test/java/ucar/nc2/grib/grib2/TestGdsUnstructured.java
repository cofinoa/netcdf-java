package ucar.nc2.grib.grib2;

import org.junit.Test;
import ucar.ma2.Array;
import ucar.nc2.AttributeContainer;
import ucar.nc2.Group;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.NetcdfDatasets;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.*;

public class TestGdsUnstructured {

  @Test
  public void testParseFromBytes() {
    // Construct a raw GDS (Section 3) byte array for an icosahedral grid.
    // We'll use a very coarse grid (e.g., n2=1, n3=0 => Ni = 2^1 * 3^0 = 2 intervals).
    byte[] gdsBytes = new byte[35];
    // Set Section 3 length (bytes 0-3) and section number (byte 4)
    int length = gdsBytes.length;
    gdsBytes[0] = 0;
    gdsBytes[1] = 0;
    gdsBytes[2] = (byte) ((length >> 8) & 0xFF);
    gdsBytes[3] = (byte) (length & 0xFF);
    gdsBytes[4] = 3; // Section number = 3

    // Source of grid definition = 0 (specified in Section 3)
    gdsBytes[5] = 0;
    int totalPoints = 20;
    // Fill totalPoints in bytes 6-9 (32-bit int)
    gdsBytes[6] = (byte) ((totalPoints >> 24) & 0xFF);
    gdsBytes[7] = (byte) ((totalPoints >> 16) & 0xFF);
    gdsBytes[8] = (byte) ((totalPoints >> 8) & 0xFF);
    gdsBytes[9] = (byte) (totalPoints & 0xFF);
    // No optional list (byte 10 = 0 length, byte 11 = 0)
    gdsBytes[10] = 0;
    gdsBytes[11] = 0;
    // Template number 3.101 (bytes 12-13)
    gdsBytes[12] = 0;
    gdsBytes[13] = 101;

    int earthShape = 6;
    gdsBytes[14] = (byte) earthShape; // shape of the earth

    int numberOfGridUsed = 26;
    gdsBytes[15] = (byte) ((numberOfGridUsed >> 16) & 0xFF);
    gdsBytes[16] = (byte) ((numberOfGridUsed >> 8) & 0xFF);
    gdsBytes[17] = (byte) (numberOfGridUsed & 0xFF);

    int numberOfGridInReference = 162;
    gdsBytes[18] = (byte) numberOfGridInReference; // number of grid in reference

    UUID uuid = UUID.fromString("a27b8de6-18c4-11e4-820a-b5b098c6a5c0");
    long msb = uuid.getMostSignificantBits();
    long lsb = uuid.getLeastSignificantBits();

    // Fill UUID bytes 19-34
    // first msb (8 byte)
    gdsBytes[19] = (byte) ((msb >> 56) & 0xFF);
    gdsBytes[20] = (byte) ((msb >> 48) & 0xFF);
    gdsBytes[21] = (byte) ((msb >> 40) & 0xFF);
    gdsBytes[22] = (byte) ((msb >> 32) & 0xFF);
    gdsBytes[23] = (byte) ((msb >> 24) & 0xFF);
    gdsBytes[24] = (byte) ((msb >> 16) & 0xFF);
    gdsBytes[25] = (byte) ((msb >> 8) & 0xFF);
    gdsBytes[26] = (byte) (msb & 0xFF);
    // then lsb (8 byte)
    gdsBytes[27] = (byte) ((lsb >> 56) & 0xFF);
    gdsBytes[28] = (byte) ((lsb >> 48) & 0xFF);
    gdsBytes[29] = (byte) ((lsb >> 40) & 0xFF);
    gdsBytes[30] = (byte) ((lsb >> 32) & 0xFF);
    gdsBytes[31] = (byte) ((lsb >> 24) & 0xFF);
    gdsBytes[32] = (byte) ((lsb >> 16) & 0xFF);
    gdsBytes[33] = (byte) ((lsb >> 8) & 0xFF);
    gdsBytes[34] = (byte) (lsb & 0xFF);


    // Parse the byte array into a Grib2Gds (should produce a Grib2Gds.GdsUnstructured instance)
    Grib2Gds gds = Grib2Gds.factory(101, gdsBytes);
    assertNotNull("Parsed GDS should not be null", gds);
    assertTrue("Factory should return Grib2Gds.GdsUnstructured instance", gds instanceof Grib2Gds.GdsUnstructured);
    Grib2Gds.GdsUnstructured usGds = (Grib2Gds.GdsUnstructured) gds;

    // Verify the parsed fields
    assertEquals("Template number", 101, usGds.template);

    assertEquals("numberOfDataPoints", totalPoints, usGds.numberOfDataPoints);
    assertEquals("earthShape", earthShape, usGds.earthShape);

    assertEquals("numberOfGridUsed", numberOfGridUsed, usGds.numberOfGridUsed);
    assertEquals("numberOfGridInReference", numberOfGridInReference, usGds.numberOfGridInReference);
    assertEquals("horizontalGridUUID", uuid, usGds.horizontalGridUUID);
    assertEquals("isLatLon", false, usGds.isLatLon());
  }

  // @Test
  public void testIconGrib2FileRead() throws IOException {
    // TODO: upload data somewhere (5.9 MB)
    String iconFile = ".../icon_global_icosahedral_single-level_2025031912_004_T_2M.grib2.bz2";
    // Open the GRIB2 file as a NetcdfDataset (netCDF-Java will handle the GRIB indexing and reading)
    try (NetcdfDataset ds = NetcdfDatasets.openDataset(iconFile)) {
      // Ensure the dataset opened
      assertNotNull("NetcdfDataset should be opened", ds);

      Group rootGroup = ds.getRootGroup();
      assertNotNull("Root group should not be null", rootGroup);

      int length = 2949120;
      assertEquals("x dimension", length, rootGroup.findDimension("x").getLength());
      assertEquals("y dimension", 1, rootGroup.findDimension("y").getLength());
      assertEquals("time dimension", 1, rootGroup.findDimension("time").getLength());
      assertEquals("height_above_ground dimension", 1, rootGroup.findDimension("height_above_ground").getLength());

      Variable projection = rootGroup.findVariableLocal("GdsUnstructured_Projection");
      assertNotNull("projection should not be null", projection);
      AttributeContainer projectionAttributes = projection.attributes();
      assertEquals("earth_shape", 6d, projectionAttributes.findAttributeDouble("earth_shape", Double.NaN), 0);
      assertEquals("number_of_grid_used", 26d,
          projectionAttributes.findAttributeDouble("number_of_grid_used", Double.NaN), 0);
      assertEquals("number_of_grid_in_reference", 162d,
          projectionAttributes.findAttributeDouble("number_of_grid_in_reference", Double.NaN), 0);
      assertEquals("uuid", "a27b8de6-18c4-11e4-820a-b5b098c6a5c0",
          projectionAttributes.findAttributeString("uuid", null));

      float[] heightAboveGround =
          (float[]) rootGroup.findVariableLocal("height_above_ground").read().copyToNDJavaArray();
      assertEquals("heightAboveGround length", 1, heightAboveGround.length);
      assertEquals("heightAboveGround", 2f, heightAboveGround[0], 0);

      Variable ddata1 = rootGroup.findVariableLocal("Temperature_height_above_ground");
      Array imageDataA = ddata1.read();
      float[][][][] thag = (float[][][][]) imageDataA.copyToNDJavaArray();
      assertEquals("data dimension 0", 1, thag.length);
      assertEquals("data dimension 1", 1, thag[0].length);
      assertEquals("data dimension 2", 1, thag[0][0].length);
      assertEquals("data dimension 3", length, thag[0][0][0].length);
    }
  }

}
