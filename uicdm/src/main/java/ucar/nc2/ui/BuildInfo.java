/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package ucar.nc2.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BuildInfo {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BuildInfo.class);

  private final String version, timestamp;

  BuildInfo(String version, String timestamp) {
    this.version = version;
    this.timestamp = timestamp;
  }

  public String getVersion() {
    return version;
  }

  public String getTimestamp() {
    return timestamp;
  }

  static BuildInfo getToolsUIBuildInfo() {
    Properties buildProps = new Properties();
    try (InputStream stream = ToolsUI.class.getClassLoader().getResourceAsStream("toolsui.properties")) {
      buildProps.load(stream);
    } catch (IOException e) {
      log.error("Error reading build properties");
    }
    return new BuildInfo(buildProps.getProperty("toolsui.version", "Unknown"),
        buildProps.getProperty("toolsui.buildTimestamp", "Unknown"));
  }

}
