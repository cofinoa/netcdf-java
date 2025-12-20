/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package edu.ucar.unidata.compression.jna.libblosc2;

import static com.google.common.truth.Truth.assertThat;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.NativeLongByReference;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestLibBlosc2 {

  private static float[] sourceData;
  private static int sourceSizeBytes;
  private static Memory sourceMemory;

  @BeforeClass
  public static void init() {
    // generate data
    int numberOfValues = 100;
    sourceData = new float[numberOfValues];
    for (int i = 0; i < numberOfValues; i++) {
      sourceData[i] = i * 0.15f;
    }
    sourceSizeBytes = sourceData.length * Float.BYTES;
    sourceMemory = new Memory(sourceSizeBytes);
    sourceMemory.write(0, sourceData, 0, sourceData.length);
    LibBlosc2.init();
  }

  @AfterClass
  public static void destroy() {
    sourceMemory.close();
    LibBlosc2.destroy();
  }

  @Test
  public void testRoundTrip() {
    float[] dataRoundTrip;

    // compress data
    try (Memory compressedMemory = new Memory(sourceSizeBytes)) {
      int compressedSize = LibBlosc2.blosc1_compress(2, 1, new NativeLong(4), new NativeLong(sourceSizeBytes),
          sourceMemory, compressedMemory, new NativeLong(sourceSizeBytes));

      // verify that the compressed size is smaller than the original
      assertThat(compressedSize).isLessThan((int) sourceSizeBytes);

      NativeLongByReference nbytes = new NativeLongByReference();
      NativeLongByReference cbytes = new NativeLongByReference();
      NativeLongByReference blocksize = new NativeLongByReference();
      LibBlosc2.blosc1_cbuffer_sizes(compressedMemory.getByteArray(0, compressedSize), nbytes, cbytes, blocksize);

      // check that nbytes is equal to the original size
      assertThat(nbytes.getValue()).isEqualTo(new NativeLong(sourceSizeBytes));
      // check that cbytes is equal to the size returned by blosc1_compress
      assertThat(cbytes.getValue()).isEqualTo(new NativeLong(compressedSize));

      // decompress the freshly compressed data
      byte[] compressedBytes = compressedMemory.getByteArray(0, cbytes.getValue().intValue());
      Memory decompressedMemory = new Memory(sourceSizeBytes);
      LibBlosc2.blosc1_decompress(compressedBytes, decompressedMemory, new NativeLong(sourceSizeBytes));
      dataRoundTrip = decompressedMemory.getFloatArray(0, sourceData.length);
    }

    // check that round tripped data is the same as the source data
    assertThat(dataRoundTrip).isEqualTo(sourceData);
  }

  @Test
  public void testDecompressHelper() {
    byte[] compressedBytes, decompressedBytes;

    try (Memory compressedMemory = new Memory(sourceSizeBytes)) {
      int compressedSize = LibBlosc2.blosc1_compress(2, 1, new NativeLong(4), new NativeLong(sourceSizeBytes),
          sourceMemory, compressedMemory, new NativeLong(sourceSizeBytes));

      // verify that the compressed size is smaller than the original
      assertThat(compressedSize).isLessThan((int) sourceSizeBytes);

      // decompress the freshly compressed data
      compressedBytes = compressedMemory.getByteArray(0, compressedSize);
      Memory decompressedMemory = new Memory(sourceSizeBytes);
      LibBlosc2.blosc1_decompress(compressedBytes, decompressedMemory, new NativeLong(sourceSizeBytes));
      decompressedBytes = decompressedMemory.getByteArray(0, sourceSizeBytes);
    }

    assertThat(decompressedBytes).isNotNull();
    assertThat(decompressedBytes.length).isEqualTo(sourceSizeBytes);
    byte[] helperDecompressedBytes = LibBlosc2.decode(compressedBytes);
    assertThat(helperDecompressedBytes).isEqualTo(decompressedBytes);
  }

}
