
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
package com.vividsolutions.jts.noding.snapround;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.algorithm.*;
import com.vividsolutions.jts.noding.*;

/**
 * Uses Snap Rounding to compute a rounded,
 * fully noded arrangement from a set of {@link SegmentString}s.
 * Implements the Snap Rounding technique described in Hobby, Guibas & Marimont,
 * and Goodrich et al.
 * Snap Rounding assumes that all vertices lie on a uniform grid
 * (hence the precision model of the input must be fixed precision,
 * and all the input vertices must be rounded to that precision).
 * <p>
 * This implementation uses simple iteration over the line segments.
 * <p>
 * This implementation appears to be fully robust using an integer precision model.
 * It will function with non-integer precision models, but the
 * results are not 100% guaranteed to be correctly noded.
 *
 * @version 1.7
 */
public class SimpleSnapRounder
    implements Noder
{
  private final PrecisionModel pm;
  private LineIntersector li;
  private final double scaleFactor;
  private Collection nodedSegStrings;

  public SimpleSnapRounder(PrecisionModel pm) {
    this.pm = pm;
    li = new RobustLineIntersector();
    li.setPrecisionModel(pm);
    scaleFactor = pm.getScale();
  }

  public Collection getNodedSubstrings()
  {
    return  SegmentString.getNodedSubstrings(nodedSegStrings);
  }

  public void computeNodes(Collection inputSegmentStrings)
  {
    this.nodedSegStrings = inputSegmentStrings;
    snapRound(inputSegmentStrings, li);

    // testing purposes only - remove in final version
    //checkCorrectness(inputSegmentStrings);
  }

  private void checkCorrectness(Collection inputSegmentStrings)
  {
    Collection resultSegStrings = SegmentString.getNodedSubstrings(inputSegmentStrings);
    NodingValidator nv = new NodingValidator(resultSegStrings);
    try {
      nv.checkValid();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  private void snapRound(Collection segStrings, LineIntersector li)
  {
    List intersections = findInteriorIntersections(segStrings, li);
    computeSnaps(segStrings, intersections);
    computeVertexSnaps(segStrings);
  }

  /**
   * Computes all interior intersections in the collection of {@link SegmentString}s,
   * and returns their @link Coordinate}s.
   *
   * Does NOT node the segStrings.
   *
   * @return a list of Coordinates for the intersections
   */
  private List findInteriorIntersections(Collection segStrings, LineIntersector li)
  {
    IntersectionFinderAdder intFinderAdder = new IntersectionFinderAdder(li);
    SinglePassNoder noder = new MCIndexNoder();
    noder.setSegmentIntersector(intFinderAdder);
    noder.computeNodes(segStrings);
    return intFinderAdder.getInteriorIntersections();
  }


  /**
   * Computes nodes introduced as a result of snapping segments to snap points (hot pixels)
   * @param li
   */
  private void computeSnaps(Collection segStrings, Collection snapPts)
  {
    for (Iterator i0 = segStrings.iterator(); i0.hasNext(); ) {
      SegmentString ss = (SegmentString) i0.next();
      computeSnaps(ss, snapPts);
    }
  }

  private void computeSnaps(SegmentString ss, Collection snapPts)
  {
    for (Iterator it = snapPts.iterator(); it.hasNext(); ) {
      Coordinate snapPt = (Coordinate) it.next();
      HotPixel hotPixel = new HotPixel(snapPt, scaleFactor, li);
      for (int i = 0; i < ss.size() - 1; i++) {
        addSnappedNode(hotPixel, ss, i);
      }
    }
  }

  /**
   * Computes nodes introduced as a result of
   * snapping segments to vertices of other segments
   *
   * @param segStrings the list of segment strings to snap together
   */
  public void computeVertexSnaps(Collection edges)
  {
    for (Iterator i0 = edges.iterator(); i0.hasNext(); ) {
      SegmentString edge0 = (SegmentString) i0.next();
      for (Iterator i1 = edges.iterator(); i1.hasNext(); ) {
        SegmentString edge1 = (SegmentString) i1.next();
        computeVertexSnaps(edge0, edge1);
      }
    }
  }

  /**
   * Performs a brute-force comparison of every segment in each {@link SegmentString}.
   * This has n^2 performance.
   */
  private void computeVertexSnaps(SegmentString e0, SegmentString e1)
  {
    Coordinate[] pts0 = e0.getCoordinates();
    Coordinate[] pts1 = e1.getCoordinates();
    for (int i0 = 0; i0 < pts0.length - 1; i0++) {
      HotPixel hotPixel = new HotPixel(pts0[i0], scaleFactor, li);
      for (int i1 = 0; i1 < pts1.length - 1; i1++) {
        // don't snap a vertex to itself
        if (e0 == e1) {
          if (i0 == i1) continue;
        }
        //System.out.println("trying " + pts0[i0] + " against " + pts1[i1] + pts1[i1 + 1]);
        boolean isNodeAdded = addSnappedNode(hotPixel, e1, i1);
        // if a node is created for a vertex, that vertex must be noded too
        if (isNodeAdded) {
          e0.addIntersection(pts0[i0], i0);
        }
      }
    }
  }

  /**
   * Adds a new node (equal to the snap pt) to the segment
   * if the segment passes through the hot pixel
   *
   * @param hotPix
   * @param segStr
   * @param segIndex
   * @return <code>true</code> if a node was added
   */
  public static boolean addSnappedNode(
      HotPixel hotPix,
      SegmentString segStr,
      int segIndex
      )
  {
    Coordinate p0 = segStr.getCoordinate(segIndex);
    Coordinate p1 = segStr.getCoordinate(segIndex + 1);

    if (hotPix.intersects(p0, p1)) {
      //System.out.println("snapped: " + snapPt);
      //System.out.println("POINT (" + snapPt.x + " " + snapPt.y + ")");
      segStr.addIntersection(hotPix.getCoordinate(), segIndex);

      return true;
    }
    return false;
  }

}
