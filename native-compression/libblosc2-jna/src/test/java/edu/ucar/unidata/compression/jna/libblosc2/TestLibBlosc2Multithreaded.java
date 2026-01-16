/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package edu.ucar.unidata.compression.jna.libblosc2;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestLibBlosc2Multithreaded {
  private static final Random r = new Random();

  AtomicReference<AssertionError> failed = new AtomicReference<>();
  CountDownLatch startupLatch, readyLatch, finishedLatch;
  boolean testDebugMessages = true;

  @BeforeClass
  public static void init() {
    LibBlosc2.init();
  }

  @AfterClass
  public static void destroy() {
    LibBlosc2.destroy();
  }

  static class Blosc2TestCase {
    byte[] expectedDecompressedData;
    byte[] compressedData;

    Blosc2TestCase(double[] sourceData, int clevel, int doshuffle) {
      int sourceSizeInBytes = sourceData.length * Double.BYTES;
      try (Memory sourceMemory = new Memory(sourceSizeInBytes);
          Memory compressedMemory = new Memory(sourceSizeInBytes)) {
        sourceMemory.write(0, sourceData, 0, sourceData.length);
        int compressedSize = LibBlosc2.blosc1_compress(clevel, doshuffle, new NativeLong(Double.BYTES),
            new NativeLong(sourceSizeInBytes), sourceMemory, compressedMemory, new NativeLong(sourceSizeInBytes));
        assertThat(compressedSize).isLessThan(sourceSizeInBytes);
        assertThat(compressedSize).isNotEqualTo(0);
        this.compressedData = compressedMemory.getByteArray(0, compressedSize);
        this.expectedDecompressedData = sourceMemory.getByteArray(0, sourceSizeInBytes);
      }
    }
  }

  public static double[] createRandom(int n) {
    double[] sourceData = new double[n];
    double scale = r.nextFloat();
    double offset = r.nextFloat();
    for (int i = 0; i < n; i++) {
      sourceData[i] = i * scale + offset;
    }
    return sourceData;
  }

  public class DecompressRunnable implements Runnable {

    Blosc2TestCase myTestCase;

    DecompressRunnable(Blosc2TestCase testCase) {
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
      assertThat(LibBlosc2.decode(myTestCase.compressedData)).isEqualTo(myTestCase.expectedDecompressedData);
      if (testDebugMessages) {
        System.out.println(Thread.currentThread().getId() + ", finished!");
      }
      finishedLatch.countDown();
    }
  }

  void runTest(Blosc2TestCase[] myTestCases) {
    startupLatch = new CountDownLatch(1);
    if (testDebugMessages) {
      System.out.println("Main thread is: " + Thread.currentThread().getName());
    }

    finishedLatch = new CountDownLatch(myTestCases.length);
    readyLatch = new CountDownLatch(myTestCases.length);
    for (Blosc2TestCase myTestCase : myTestCases) {
      Thread t = new Thread(new DecompressRunnable(myTestCase));
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
  public void testMultithreaded() {
    Blosc2TestCase[] differentParamTestCases = new Blosc2TestCase[] {new Blosc2TestCase(createRandom(500), 3, 1),
        new Blosc2TestCase(createRandom(100000), 2, 2), new Blosc2TestCase(createRandom(10000), 2, 1),
        new Blosc2TestCase(createRandom(500000), 4, 2), new Blosc2TestCase(createRandom(800000), 9, 1),
        new Blosc2TestCase(createRandom(1000000), 9, 1), new Blosc2TestCase(createRandom(700000), 8, 2),
        new Blosc2TestCase(createRandom(300000), 9, 1)};
    runTest(differentParamTestCases);
  }
}
