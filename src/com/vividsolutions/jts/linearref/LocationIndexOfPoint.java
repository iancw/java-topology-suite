package com.vividsolutions.jts.linearref;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.util.Assert;

/**
 * Computes the {@link LinearLocation} of the point
 * on a linear {@link Geometry} nearest a given {@link Coordinate}.
 * The nearest point is not necessarily unique; this class
 * always computes the nearest point closest to
 * the start of the geometry.
 */
class LocationIndexOfPoint
{
  public static LinearLocation indexOf(Geometry linearGeom, Coordinate inputPt)
  {
    LocationIndexOfPoint locater = new LocationIndexOfPoint(linearGeom);
    return locater.indexOf(inputPt);
  }

  private Geometry linearGeom;

  public LocationIndexOfPoint(Geometry linearGeom) {
    this.linearGeom = linearGeom;
  }

  /**
   * Find the nearest location along a linear {@link Geometry} to a given point.
   *
   * @param inputPt the coordinate to locate
   * @return the location of the nearest point
   */
  public LinearLocation indexOf(Coordinate inputPt)
  {
    return indexOfFromStart(inputPt, null);
  }

  /**
   * Find the nearest {@link LinearLocation} along the linear {@link Geometry}
   * to a given {@link Coordinate}
   * after the specified minimum {@link LinearLocation}.
   * If possible the location returned will be strictly greater than the
   * <code>minLocation</code>.
   * If this is not possible, the
   * value returned will equal <code>minLocation</code>.
   * (An example where this is not possible is when
   * minLocation = [end of line] ).
   *
   * @param inputPt the coordinate to locate
   * @param minLocation the minimum location for the point location
   * @return the location of the nearest point
   */
  public LinearLocation indexOfAfter(Coordinate inputPt, LinearLocation minIndex)
  {
    if (minIndex == null) return indexOf(inputPt);

    // sanity check for minLocation at or past end of line
    LinearLocation endLoc = LinearLocation.getEndLocation(linearGeom);
    if (endLoc.compareTo(minIndex) <= 0)
      return endLoc;

    LinearLocation closestAfter = indexOfFromStart(inputPt, minIndex);
    /**
     * Return the minDistanceLocation found.
     * This will not be null, since it was initialized to minLocation
     */
    Assert.isTrue(closestAfter.compareTo(minIndex) >= 0,
                  "computed location is before specified minimum location");
    return closestAfter;
  }

  private LinearLocation indexOfFromStart(Coordinate inputPt, LinearLocation minIndex)
  {
    double minDistance = Double.MAX_VALUE;
    int minComponentIndex = 0;
    int minSegmentIndex = 0;
    double minFrac = -1.0;

    LineSegment seg = new LineSegment();
    for (LinearIterator it = new LinearIterator(linearGeom);
         it.hasNext(); it.next()) {
      if (! it.isEndOfLine()) {
        seg.p0 = it.getSegmentStart();
        seg.p1 = it.getSegmentEnd();
        double segDistance = seg.distance(inputPt);
        double segFrac = segmentFraction(seg, inputPt);

        int candidateComponentIndex = it.getComponentIndex();
        int candidateSegmentIndex = it.getVertexIndex();
        if (segDistance < minDistance) {
          // ensure after minLocation, if any
          if (minIndex == null ||
              minIndex.compareLocationValues(
              candidateComponentIndex, candidateSegmentIndex, segFrac)
              < 0
              ) {
            // otherwise, save this as new minimum
            minComponentIndex = candidateComponentIndex;
            minSegmentIndex = candidateSegmentIndex;
            minFrac = segFrac;
            minDistance = segDistance;
          }
        }
      }
    }
    LinearLocation loc = new LinearLocation(minComponentIndex, minSegmentIndex, minFrac);
    return loc;
  }

  public static double segmentFraction(
      LineSegment seg,
      Coordinate inputPt)
  {
    double segFrac = seg.projectionFactor(inputPt);
    if (segFrac < 0.0)
      segFrac = 0.0;
    else if (segFrac > 1.0)
      segFrac = 1.0;
    return segFrac;
  }
}
