/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package edu.ucar.unidata.compression.jna.libaec;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class TestLoadLibAec {

  @Test
  public void testBasicLoad() {
    LibAec libAec = new LibAec();
    assertThat(libAec).isNotNull();
  }
}
