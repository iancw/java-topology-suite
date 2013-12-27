

/*
 * The JTS Topology Suite is a collection of Java classes that
 * implement the fundamental operations required to validate a given
 * geo-spatial data set to a known topological specification.
 *
 * Copyright (C) 2001 Vivid Solutions
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For more information, contact:
 *
 *     Vivid Solutions
 *     Suite #1A
 *     2328 Government Street
 *     Victoria BC  V8T 5G5
 *     Canada
 *
 *     (250)385-6040
 *     www.vividsolutions.com
 */
package com.vividsolutions.jts.geom;

/**
 * Models an OGC SFS <code>LinearRing</code>.
 * A LinearRing is a LineString which is both closed and simple.
 * In other words,
 * the first and last coordinate in the ring must be equal,
 * and the interior of the ring must not self-intersect.
 * Either orientation of the ring is allowed.
 *
 * @version 1.7
 */
public class LinearRing extends LineString
{
  private static final long serialVersionUID = -4261142084085851829L;

  /**
   * Constructs a <code>LinearRing</code> with the given points.
   *
   *@param  points          points forming a closed and simple linestring, or
   *      <code>null</code> or an empty array to create the empty geometry.
   *      This array must not contain <code>null</code> elements.
   *
   *@param  precisionModel  the specification of the grid of allowable points
   *      for this <code>LinearRing</code>
   *@param  SRID            the ID of the Spatial Reference System used by this
   *      <code>LinearRing</code>
   * @deprecated Use GeometryFactory instead
   */
  public LinearRing(Coordinate points[], PrecisionModel precisionModel,
                    int SRID) {
    this(points, new GeometryFactory(precisionModel, SRID));
    validateConstruction();
  }

  /**
   * This method is ONLY used to avoid deprecation warnings.
   * @param points
   * @param factory
   */
  private LinearRing(Coordinate points[], GeometryFactory factory) {
    this(factory.getCoordinateSequenceFactory().create(points), factory);
  }


  /**
   * Constructs a <code>LinearRing</code> with the vertices
   * specifed by the given {@link CoordinateSequence}.
   *
   *@param  points  a sequence points forming a closed and simple linestring, or
   *      <code>null</code> to create the empty geometry.
   *
   */
  public LinearRing(CoordinateSequence points, GeometryFactory factory) {
    super(points, factory);
    validateConstruction();
  }

  private void validateConstruction() {
    if (!isEmpty() && ! super.isClosed()) {
      throw new IllegalArgumentException("points must form a closed linestring");
    }
    if (getCoordinateSequence().size() >= 1 && getCoordinateSequence().size() <= 3) {
      throw new IllegalArgumentException("Number of points must be 0 or >3");
    }
  }

  /**
   * Returns <code>Dimension.FALSE</code>, since by definition LinearRings do
   * not have a boundary.
   *
   * @return Dimension.FALSE
   */
  public int getBoundaryDimension() {
    return Dimension.FALSE;
  }

  /**
   * Returns <code>true</code>, since by definition LinearRings are always simple.
   * @return <code>true</code>
   *
   * @see Geometry#isSimple
   */
  public boolean isSimple() {
    return true;
  }

  public String getGeometryType() {
    return "LinearRing";
  }

}
