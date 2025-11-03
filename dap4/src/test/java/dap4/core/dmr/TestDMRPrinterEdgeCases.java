/*
 * Copyright (c) 2025 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package dap4.core.dmr;

import static com.google.common.truth.Truth.assertThat;

import dap4.core.util.IndentWriter;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Test;

public class TestDMRPrinterEdgeCases {
  @Test
  public void testNullValueAttr() throws IOException {
    DapAttribute attr = new DapAttribute("name", DapType.STRING);
    attr.setValues(new String[] {});
    DMRPrinter dmrPrinter = new DMRPrinter();
    StringWriter sw = new StringWriter();
    dmrPrinter.printer = new IndentWriter(sw);
    dmrPrinter.printAttribute(attr);
    String encodedAttribute = sw.toString();
    assertThat(encodedAttribute).isNotEmpty();
    assertThat(encodedAttribute).ignoringCase().contains("<value/>");
  }
}
