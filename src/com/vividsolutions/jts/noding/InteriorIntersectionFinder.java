package com.vividsolutions.jts.noding;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.algorithm.LineIntersector;
//import com.vividsolutions.jts.util.Debug;

/**
 * Finds an interior intersection in a set of {@link SegmentString}s,
 * if one exists.  Only the first intersection found is reported.
 *
 * @version 1.7
 */
public class InteriorIntersectionFinder
    implements SegmentIntersector
{
	private boolean isCheckEndSegmentsOnly = false;
  private LineIntersector li;
  private Coordinate interiorIntersection = null;
  private Coordinate[] intSegments = null;

  /**
   * Creates an intersection finder which finds an interior intersection
   * if one exists
   *
   * @param li the LineIntersector to use
   */
  public InteriorIntersectionFinder(LineIntersector li)
  {
    this.li = li;
    interiorIntersection = null;
  }

  /**
   * Sets whether only end segments should be tested for interior intersection.
   * This is a performance optimization that may be used if
   * the segments have been previously noded by an appropriate algorithm.
   * It may be known that any potential noding failures will occur only in
   * end segments.
   * 
   * @param isCheckEndSegmentsOnly whether to test only end segments
   */
  public void setCheckEndSegmentsOnly(boolean isCheckEndSegmentsOnly)
  {
  	this.isCheckEndSegmentsOnly = isCheckEndSegmentsOnly;
  }
  
  /**
   * Tests whether an intersection was found.
   * 
   * @return true if an intersection was found
   */
  public boolean hasIntersection() 
  { 
  	return interiorIntersection != null; 
  }
  
  /**
   * Gets the computed location of the intersection.
   * Due to round-off, the location may not be exact.
   * 
   * @return the coordinate for the intersection location
   */
  public Coordinate getInteriorIntersection()  
  {    
  	return interiorIntersection;  
  }

  /**
   * Gets the endpoints of the intersecting segments.
   * 
   * @return an array of the segment endpoints (p00, p01, p10, p11)
   */
  public Coordinate[] getIntersectionSegments()
  {
  	return intSegments;
  }
  
  /**
   * This method is called by clients
   * of the {@link SegmentIntersector} class to process
   * intersections for two segments of the {@link SegmentStrings} being intersected.
   * Note that some clients (such as {@link MonotoneChain}s) may optimize away
   * this call for segment pairs which they have determined do not intersect
   * (e.g. by an disjoint envelope test).
   */
  public void processIntersections(
      SegmentString e0,  int segIndex0,
      SegmentString e1,  int segIndex1
      )
  {
  	// short-circuit if intersection already found
  	if (hasIntersection())
  		return;
  	
    // don't bother intersecting a segment with itself
    if (e0 == e1 && segIndex0 == segIndex1) return;

    /**
     * If enabled, only test end segments (on either segString).
     * 
     */
    if (isCheckEndSegmentsOnly) {
    	boolean isEndSegPresent = isEndSegment(e0, segIndex0) || isEndSegment(e1, segIndex1);
    	if (! isEndSegPresent)
    		return;
    }
    
    Coordinate p00 = e0.getCoordinates()[segIndex0];
    Coordinate p01 = e0.getCoordinates()[segIndex0 + 1];
    Coordinate p10 = e1.getCoordinates()[segIndex1];
    Coordinate p11 = e1.getCoordinates()[segIndex1 + 1];
    
    li.computeIntersection(p00, p01, p10, p11);
//if (li.hasIntersection() && li.isProper()) Debug.println(li);

    if (li.hasIntersection()) {
      if (li.isInteriorIntersection()) {
      	intSegments = new Coordinate[4];
      	intSegments[0] = p00;
      	intSegments[1] = p01;
      	intSegments[2] = p10;
      	intSegments[3] = p11;
      	
      	interiorIntersection = li.getIntersection(0);
      }
    }
  }
  
  /**
   * Tests whether a segment in a {@link SegmentString} is an end segment.
   * (either the first or last).
   * 
   * @param segStr a segment string
   * @param index the index of a segment in the segment string
   * @return true if the segment is an end segment
   */
  private boolean isEndSegment(SegmentString segStr, int index)
  {
  	if (index == 0) return true;
  	if (index >= segStr.size() - 2) return true;
  	return false;
  }
  
  public boolean isDone()
  { 
  	return interiorIntersection != null;
  }
}