/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package edu.ucar.unidata.compression.jna.libaec;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static edu.ucar.unidata.compression.jna.libaec.LibAec.AEC_DATA_3BYTE;
import static edu.ucar.unidata.compression.jna.libaec.LibAec.AEC_DATA_MSB;
import static edu.ucar.unidata.compression.jna.libaec.LibAec.AEC_DATA_PREPROCESS;
import static edu.ucar.unidata.compression.jna.libaec.LibAec.AEC_DATA_SIGNED;
import static edu.ucar.unidata.compression.jna.libaec.LibAec.AEC_OK;
import static edu.ucar.unidata.compression.jna.libaec.LibAec.AEC_RESTRICTED;

import com.sun.jna.Memory;
import edu.ucar.unidata.compression.jna.libaec.LibAec.AecStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import org.junit.Test;

/**
 * Test multithreaded access to native libaec by round tripping data in multiple threads
 */
public class TestLibAecMultithreaded {

  AtomicReference<AssertionError> failed = new AtomicReference<>();
  CountDownLatch startupLatch, readyLatch, finishedLatch;
  boolean testDebugMessages = true;

  static class AecTestCase {
    int[] origData;
    int bitsPerSample, blockSize, rsi, flags;

    AecTestCase(int[] origData, int bitsPerSample, int blockSize, int rsi, int flags) {
      this.origData = origData;
      this.bitsPerSample = bitsPerSample;
      this.blockSize = blockSize;
      this.rsi = rsi;
      this.flags = flags;
    }
  }

  public static int[] createRandom(int n) {
    Random r = new Random();
    int min = -10000;
    int max = 10000;
    return IntStream.generate(() -> r.nextInt(max - min) + min).limit(n).toArray();
  }

  public class RoundTripRunnable implements Runnable {

    AecTestCase myTestCase;

    RoundTripRunnable(AecTestCase testCase) {
      this.myTestCase = testCase;
    }

    @Override
    public void run() {
      if (testDebugMessages) {
        System.out.println(Thread.currentThread().getId() + ", awaiting execution signal");
      }
      try {
        readyLatch.countDown();
        boolean startupReady = startupLatch.await(1, TimeUnit.SECONDS);
        assertWithMessage("test startup took too long").that(startupReady).isTrue();
      } catch (InterruptedException e) {
        failed.set(new AssertionError("test startup took too long", e));;
      }
      if (testDebugMessages) {
        System.out.println(Thread.currentThread().getId() + ", executing run() method!");
      }
      // byte array to hold the encoded data
      byte[] encodedData;

      // convert input data into byte array
      ByteBuffer bb = ByteBuffer.allocate(myTestCase.origData.length * Integer.BYTES);
      bb.order(ByteOrder.nativeOrder());
      bb.asIntBuffer().put(myTestCase.origData);
      byte[] inputData = bb.array();
      final int maxTotalOutBytes = myTestCase.origData.length * Integer.BYTES * 67 / 64 + 256;
      try {
        // initialize native memory to hold input and output data
        try (Memory inputMemory = new Memory(inputData.length * Byte.BYTES);
            Memory outputMemory = new Memory(maxTotalOutBytes)) {

          // set encoding parameters
          AecStream aecStreamDecode =
              AecStream.create(myTestCase.bitsPerSample, myTestCase.blockSize, myTestCase.rsi, myTestCase.flags);

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
            Memory outputMemory = new Memory((long) myTestCase.origData.length * Integer.BYTES)) {

          // set encoding parameters
          AecStream aecStreamDecode =
              AecStream.create(myTestCase.bitsPerSample, myTestCase.blockSize, myTestCase.rsi, myTestCase.flags);

          // load encoded data into memory
          inputMemory.write(0, encodedData, 0, encodedData.length);

          aecStreamDecode.setInputMemory(inputMemory);
          aecStreamDecode.setOutputMemory(outputMemory);

          // decode
          int ok = LibAec.aec_buffer_decode(aecStreamDecode);
          assertThat(ok).isEqualTo(AEC_OK);

          // check expected number of bytes decoded
          assertThat(aecStreamDecode.total_out.intValue()).isEqualTo(myTestCase.origData.length * Integer.BYTES);

          // read decoded data from native memory
          int[] decodedData = new int[aecStreamDecode.total_out.intValue() / Integer.BYTES];
          outputMemory.read(0, decodedData, 0, decodedData.length);

          // compare decoded data to original values
          for (int i = 0; i < decodedData.length; i++) {
            assertThat(decodedData[i]).isEqualTo(myTestCase.origData[i]);
          }
        }
        if (testDebugMessages) {
          System.out.println(Thread.currentThread().getId() + ", finished!");
        }
      } catch (AssertionError ae) {
        failed.set(ae);
      } finally {
        finishedLatch.countDown();
      }
    }
  }

  void runTest(AecTestCase[] myTestCases) {
    startupLatch = new CountDownLatch(1);
    if (testDebugMessages) {
      System.out.println("Main thread is: " + Thread.currentThread().getName());
    }


    finishedLatch = new CountDownLatch(myTestCases.length);
    readyLatch = new CountDownLatch(myTestCases.length);
    for (AecTestCase myTestCase : myTestCases) {
      Thread t = new Thread(new RoundTripRunnable(myTestCase));
      t.start();
    }
    // ensure threaded tests start running at the same time
    try {
      boolean testThreadsReady = readyLatch.await(1, TimeUnit.SECONDS);
      assertWithMessage("test threads took too long to prepare").that(testThreadsReady).isTrue();
    } catch (InterruptedException e) {
      failed.set(new AssertionError("test threads took too long to prepare", e));;
    }
    // trigger threaded tests to start running
    if (testDebugMessages) {
      System.out.println("Start testing threads from: " + Thread.currentThread().getName());
    }
    startupLatch.countDown();
    // wait for tests to finish
    try {
      boolean threadsComplete = finishedLatch.await(5, TimeUnit.SECONDS);
      assertWithMessage("test threads failed to complete").that(threadsComplete).isTrue();
    } catch (InterruptedException e) {
      failed.set(new AssertionError("test threads took too long to complete", e));;
    }
    if (failed.get() != null) {
      throw failed.get();
    }
  }

  @Test
  public void testMultithreadedDifferentParams() {
    AecTestCase[] differentParamTestCases =
        new AecTestCase[] {new AecTestCase(createRandom(2500), 32, 32, 64, AEC_DATA_SIGNED),
            new AecTestCase(createRandom(210), 32, 16, 128, AEC_DATA_MSB | AEC_DATA_PREPROCESS),
            new AecTestCase(createRandom(24), 32, 8, 128, AEC_DATA_3BYTE | AEC_DATA_PREPROCESS),
            new AecTestCase(createRandom(2400), 32, 16, 128, AEC_RESTRICTED),
            new AecTestCase(createRandom(29), 32, 32, 128, AEC_DATA_PREPROCESS),
            new AecTestCase(createRandom(255), 32, 8, 128, AEC_DATA_SIGNED | AEC_DATA_MSB | AEC_DATA_PREPROCESS),
            new AecTestCase(createRandom(2045), 32, 16, 128, AEC_DATA_3BYTE)};
    runTest(differentParamTestCases);
  }

  @Test
  public void testMultiThreadedSameParams() {
    AecTestCase[] sameParamTestCases =
        new AecTestCase[] {new AecTestCase(createRandom(2500), 32, 16, 128, AEC_DATA_SIGNED | AEC_DATA_PREPROCESS),
            new AecTestCase(createRandom(2500), 32, 16, 128, AEC_DATA_SIGNED | AEC_DATA_PREPROCESS),
            new AecTestCase(createRandom(2500), 32, 16, 128, AEC_DATA_SIGNED | AEC_DATA_PREPROCESS),
            new AecTestCase(createRandom(2500), 32, 16, 128, AEC_DATA_SIGNED | AEC_DATA_PREPROCESS),
            new AecTestCase(createRandom(2500), 32, 16, 128, AEC_DATA_SIGNED | AEC_DATA_PREPROCESS),
            new AecTestCase(createRandom(2500), 32, 16, 128, AEC_DATA_SIGNED | AEC_DATA_PREPROCESS),
            new AecTestCase(createRandom(2500), 32, 16, 128, AEC_DATA_SIGNED | AEC_DATA_PREPROCESS)};
    runTest(sameParamTestCases);
  }
}
