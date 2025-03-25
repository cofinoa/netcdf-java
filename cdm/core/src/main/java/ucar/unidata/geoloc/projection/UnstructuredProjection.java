package ucar.unidata.geoloc.projection;

import ucar.unidata.geoloc.*;

import java.util.Objects;

/**
 * A dummy ProjectionImpl subclass for unstructured grids.
 * Since unstructured grids do not follow a single mathematical projection,
 * this class does not implement real forward/inverse transformations.
 * Instead, it serves as a placeholder so netCDF-Java can represent the
 * grid in a coordinate system, while actual lat/lon positions are handled
 * separately (e.g., by per-cell coordinate arrays).
 */
public class UnstructuredProjection extends ProjectionImpl {

  public static final String EARTH_SHAPE = "earth_shape";
  public static final String NUMBER_OF_GRID_USED = "number_of_grid_used";
  public static final String NUMBER_OF_GRID_IN_REFERENCE = "number_of_grid_in_reference";
  public static final String UUID = "uuid";

  int earthShape, numberOfGridUsed, numberOfGridInReference;
  String uuid;

  /**
   * Create a new UnstructuredProjection with a given name.
   */
  public UnstructuredProjection(int earthShape, int numberOfGridUsed, int numberOfGridInReference, String uuid) {
    super("UnstructuredProjection", false); // false => not lat/lon

    this.earthShape = earthShape;
    this.numberOfGridUsed = numberOfGridUsed;
    this.numberOfGridInReference = numberOfGridInReference;
    this.uuid = uuid;

    addParameter(EARTH_SHAPE, earthShape);
    addParameter(NUMBER_OF_GRID_USED, numberOfGridUsed);
    addParameter(NUMBER_OF_GRID_IN_REFERENCE, numberOfGridInReference);
    addParameter(UUID, uuid);
  }

  /**
   * Copy constructor for UnstructuredProjection.
   */
  private UnstructuredProjection(UnstructuredProjection that) {
    this(that.earthShape, that.numberOfGridUsed, that.numberOfGridInReference, that.uuid);
  }

  @Override
  public ProjectionImpl constructCopy() {
    // Return a new instance with the same name, etc.
    return new UnstructuredProjection(this);
  }

  /**
   * Since we do not have a formula for an unstructured grid,
   * throw an exception if someone tries to do forward transform.
   */
  @Override
  public ProjectionPoint latLonToProj(LatLonPoint latlon, ProjectionPointImpl dest) {
    throw new UnsupportedOperationException("UnstructuredProjection: no formula-based transform available");
  }

  /**
   * Similarly, inverse transform is not defined for unstructured grids.
   */
  @Override
  public LatLonPoint projToLatLon(ProjectionPoint world, LatLonPointImpl dest) {
    throw new UnsupportedOperationException("UnstructuredProjection: no formula-based transform available");
  }

  /**
   * For unstructured grids, the notion of crossing a seam is irrelevant,
   * so we return false (or you could throw an exception).
   */
  @Override
  public boolean crossSeam(ProjectionPoint pt1, ProjectionPoint pt2) {
    return false;
  }

  @Override
  public String paramsToString() {
    return "UnstructuredProjection: no transform parameters";
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof UnstructuredProjection))
      return false;
    UnstructuredProjection that = (UnstructuredProjection) o;
    return earthShape == that.earthShape && numberOfGridUsed == that.numberOfGridUsed
        && numberOfGridInReference == that.numberOfGridInReference && Objects.equals(uuid, that.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(earthShape, numberOfGridUsed, numberOfGridInReference, uuid);
  }
}
