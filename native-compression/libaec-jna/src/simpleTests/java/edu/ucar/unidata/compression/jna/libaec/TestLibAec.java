/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package edu.ucar.unidata.compression.jna.libaec;

import static com.google.common.truth.Truth.assertThat;
import static edu.ucar.unidata.compression.jna.libaec.LibAec.AEC_DATA_PREPROCESS;
import static edu.ucar.unidata.compression.jna.libaec.LibAec.AEC_DATA_SIGNED;
import static edu.ucar.unidata.compression.jna.libaec.LibAec.AEC_OK;

import com.sun.jna.Memory;
import edu.ucar.unidata.compression.jna.libaec.LibAec.AecStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.junit.Test;

public class TestLibAec {

  private static final int[] origData = new int[] {10, 18, 31, 42, 50};

  // compute upper bound of compressed size in bytes
  // see https://gitlab.dkrz.de/k202009/libaec#output
  // In rare cases, like for random data, total_out can be larger than
  // the size of the input data total_in. The following should hold true
  // even for pathological cases.
  // total_out <= total_in * 67 / 64 + 256
  private static final int maxTotalOutBytes = origData.length * Integer.BYTES * 67 / 64 + 256;

  // unsigned byte values of encoded version of origData
  private static final int[] expectedEncoded = {24, 0, 0, 0, 80, 64, 130, 31, 254, 80, 0, 0, 0};

  // encoding parameters
  private static final int bitsPerSample = 32;
  private static final int blockSize = 16;
  private static final int rsi = 128;
  private static final int flags = AEC_DATA_SIGNED | AEC_DATA_PREPROCESS;

  @Test
  public void aecBufferEncode() {
    // convert input data into byte array
    ByteBuffer bb = ByteBuffer.allocate(origData.length * Integer.BYTES);
    bb.order(ByteOrder.nativeOrder());
    bb.asIntBuffer().put(origData);
    byte[] inputData = bb.array();

    // initialize native memory to hold input and output data
    try (Memory inputMemory = new Memory(inputData.length * Byte.BYTES);
        Memory outputMemory = new Memory(maxTotalOutBytes)) {

      // create stream decoder
      AecStream aecStreamDecode = AecStream.create(bitsPerSample, blockSize, rsi, flags);

      // load input data into native memory
      inputMemory.write(0, inputData, 0, inputData.length);
      aecStreamDecode.setInputMemory(inputMemory);

      aecStreamDecode.setOutputMemory(outputMemory);

      // encode
      int ok = LibAec.aec_buffer_encode(aecStreamDecode);
      assertThat(ok).isEqualTo(AEC_OK);

      // check expected number of bytes encoded
      assertThat(aecStreamDecode.total_out.intValue()).isEqualTo(expectedEncoded.length * Byte.BYTES);

      // read encoded data from native memory
      byte[] encodedData = new byte[aecStreamDecode.total_out.intValue()];
      outputMemory.read(0, encodedData, 0, encodedData.length);

      // compare encoded data to expect values (note: encoded values are unsigned bytes)
      for (int i = 0; i < encodedData.length; i++) {
        assertThat(Byte.toUnsignedInt(encodedData[i])).isEqualTo(expectedEncoded[i]);
      }
    }
  }

  @Test
  public void aecBufferDecode() {
    try (Memory inputMemory = new Memory(expectedEncoded.length * Byte.BYTES);
        Memory outputMemory = new Memory(origData.length * Integer.BYTES)) {
      // set encoding parameters
      AecStream aecStreamDecode = AecStream.create(bitsPerSample, blockSize, rsi, flags);

      // load expected encoded data into native memory
      byte[] expectedEncodedBytes = new byte[expectedEncoded.length];
      for (int i = 0; i < expectedEncoded.length; i++) {
        expectedEncodedBytes[i] = (byte) expectedEncoded[i];
      }
      inputMemory.write(0, expectedEncodedBytes, 0, expectedEncodedBytes.length);

      aecStreamDecode.setInputMemory(inputMemory);
      aecStreamDecode.setOutputMemory(outputMemory);

      // decode
      int ok = LibAec.aec_buffer_decode(aecStreamDecode);
      assertThat(ok).isEqualTo(AEC_OK);

      // check expected number of bytes decoded
      assertThat(aecStreamDecode.total_out.intValue()).isEqualTo(origData.length * Integer.BYTES);

      // read decoded data from native memory
      int[] decodedData = new int[aecStreamDecode.total_out.intValue() / Integer.BYTES];
      outputMemory.read(0, decodedData, 0, decodedData.length);

      // compare decoded data to original values
      for (int i = 0; i < decodedData.length; i++) {
        assertThat(decodedData[i]).isEqualTo(origData[i]);
      }
    }
  }

  @Test
  public void roundTrip() {
    // byte array to hold the encoded data
    byte[] encodedData;

    // convert input data into byte array
    ByteBuffer bb = ByteBuffer.allocate(origData.length * Integer.BYTES);
    bb.order(ByteOrder.nativeOrder());
    bb.asIntBuffer().put(origData);
    byte[] inputData = bb.array();

    // initialize native memory to hold input and output data
    try (Memory inputMemory = new Memory(inputData.length * Byte.BYTES);
        Memory outputMemory = new Memory(maxTotalOutBytes)) {

      // set encoding parameters
      AecStream aecStreamDecode = AecStream.create(bitsPerSample, blockSize, rsi, flags);

      // load input data into native memory
      inputMemory.write(0, inputData, 0, inputData.length);
      aecStreamDecode.setInputMemory(inputMemory);

      aecStreamDecode.setOutputMemory(outputMemory);

      // encode
      int ok = LibAec.aec_buffer_encode(aecStreamDecode);
      assertThat(ok).isEqualTo(AEC_OK);

      // read encoded data from native memory
      encodedData = new byte[aecStreamDecode.total_out.intValue()];
      outputMemory.read(0, encodedData, 0, encodedData.length);
    }

    assertThat(encodedData).isNotNull();

    try (Memory inputMemory = new Memory(encodedData.length * Byte.BYTES);
        Memory outputMemory = new Memory(origData.length * Integer.BYTES)) {

      // set encoding parameters
      AecStream aecStreamDecode = AecStream.create(bitsPerSample, blockSize, rsi, flags);

      // load encoded data into memory
      inputMemory.write(0, encodedData, 0, encodedData.length);

      aecStreamDecode.setInputMemory(inputMemory);
      aecStreamDecode.setOutputMemory(outputMemory);

      // decode
      int ok = LibAec.aec_buffer_decode(aecStreamDecode);
      assertThat(ok).isEqualTo(AEC_OK);

      // check expected number of bytes decoded
      assertThat(aecStreamDecode.total_out.intValue()).isEqualTo(origData.length * Integer.BYTES);

      // read decoded data from native memory
      int[] decodedData = new int[aecStreamDecode.total_out.intValue() / Integer.BYTES];
      outputMemory.read(0, decodedData, 0, decodedData.length);

      // compare decoded data to original values
      for (int i = 0; i < decodedData.length; i++) {
        assertThat(decodedData[i]).isEqualTo(origData[i]);
      }
    }
  }
}
