/*
 * Copyright 1998-2011 University Corporation for Atmospheric Research/Unidata
 *
 * Portions of this software were developed by the Unidata Program at the
 * University Corporation for Atmospheric Research.
 *
 * Access and use of this software shall impose the following obligations
 * and understandings on the user. The user is granted the right, without
 * any fee or cost, to use, copy, modify, alter, enhance and distribute
 * this software, and any derivative works thereof, and its supporting
 * documentation for any purpose whatsoever, provided that this entire
 * notice appears in all copies of the software, derivative works and
 * supporting documentation.  Further, UCAR requests that the user credit
 * UCAR/Unidata in any publications that result from the use of this
 * software or in any product that includes this software. The names UCAR
 * and/or Unidata, however, may not be used in any advertising or publicity
 * to endorse or promote any products or commercial entity unless specific
 * written permission is obtained from UCAR/Unidata. The user also
 * understands that UCAR/Unidata is not obligated to provide the user with
 * any support, consulting, training or assistance of any kind with regard
 * to the use, operation and performance of this software nor to provide
 * the user with any updates, revisions, new versions or "bug fixes."
 *
 * THIS SOFTWARE IS PROVIDED BY UCAR/UNIDATA "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL UCAR/UNIDATA BE LIABLE FOR ANY SPECIAL,
 * INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING
 * FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
 * NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION
 * WITH THE ACCESS, USE OR PERFORMANCE OF THIS SOFTWARE.
 */

/**
 *
 */
package ucar.nc2.iosp.grads;


import java.util.List;


/**
 * Extension of GradsDimension to handle the complexities of ensembles
 *
 * @author Don Murray, CU-CIRES
 */
public class GradsEnsembleDimension extends GradsDimension {

    /** ensemble names identifier */
    public static final String NAMES = "NAMES";

    /** ensemble filename template */
    public static final String ENS_TEMPLATE_ID = "%e";

    /**
     * Create a new ensemble dimension holder
     *
     * @param name  the dimension name
     * @param size  the dimension size
     * @param mapping  the dimension mapping type
     */
    public GradsEnsembleDimension(String name, int size, String mapping) {
        super(name, size, mapping);
    }

    /**
     * Get the ensemble member names
     *
     * @return the list of names
     */
    public List<String> getEnsembleNames() {
        return getLevels();
    }

    /**
     * Make the level values from the specifications
     *
     * @return the level values
     */
    protected double[] makeLevelValues() {
        double[] vals = new double[getSize()];
        for (int i = 0; i < getSize(); i++) {
            vals[i] = i;
        }
        return vals;
    }

    /**
     * Replace the ensemble template parameter in a filename
     *
     * @param filespec  the file template
     * @param ensIndex the ensemble index
     *
     * @return  the filled in template
     */
    public String replaceFileTemplate(String filespec, int ensIndex) {
        return filespec.replaceAll(ENS_TEMPLATE_ID,
                                   getEnsembleNames().get(ensIndex));
    }

}
