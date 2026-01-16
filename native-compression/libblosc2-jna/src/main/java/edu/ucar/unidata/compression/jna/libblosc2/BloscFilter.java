/*
 * Copyright (c) 2021-2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package edu.ucar.unidata.compression.jna.libblosc2;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.nc2.filter.Filter;
import ucar.nc2.filter.FilterProvider;

public class BloscFilter extends Filter {
  private static final Logger logger = LoggerFactory.getLogger(BloscFilter.class);
  private static final String name = "blosc";
  private static final int id = 32001;

  private final Map<String, Object> properties;

  public BloscFilter(Map<String, Object> properties) {
    this.properties = new HashMap<>(properties);
    logger.debug("Blosc properties = {}", this.properties);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public byte[] encode(byte[] dataIn) {
    return new byte[0];
  }

  @Override
  public byte[] decode(byte[] dataIn) {
    return LibBlosc2.decode(dataIn);
  }

  public Map<String, Object> properties() {
    return new HashMap<>(properties);
  }

  public static class Provider implements FilterProvider {

    @Override
    public String getName() {
      return name;
    }

    @Override
    public int getId() {
      return id;
    }

    @Override
    public Filter create(Map<String, Object> properties) {
      return new BloscFilter(properties);
    }
  }
}
