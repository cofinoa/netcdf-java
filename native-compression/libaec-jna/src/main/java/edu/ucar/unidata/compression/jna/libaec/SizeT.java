/*
 * Copyright (c) 1998-2018 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package edu.ucar.unidata.compression.jna.libaec;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;

/**
 * Map a native size_t with JNA.
 *
 * @see <a href="https://github.com/twall/jna/issues/191" />
 */
public class SizeT extends IntegerType {
  public SizeT() {
    this(0);
  }

  public SizeT(long value) {
    super(Native.SIZE_T_SIZE, value, true);
  }

  public String toString() {
    return String.format("%d", super.longValue());
  }
}
