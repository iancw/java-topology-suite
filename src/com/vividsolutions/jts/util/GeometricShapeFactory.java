
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
package com.vividsolutions.jts.util;

/**
 * Methods to create various geometry shapes
 */
import com.vividsolutions.jts.geom.*;

/**
 * Computes various kinds of common geometric shapes.
 * Allows various ways of specifying the location and extent of the shapes,
 * as well as number of line segments used to form them.
 * <p>
 * Example:
 * <pre>
 *  GeometricShapeFactory gsf = new GeometricShapeFactory();
 *  gsf.setSize(100);
 *  gsf.setNumPoints(100);
 *  gsf.setBase(new Coordinate(0, 0));
 *  Polygon rect = gsf.createRectangle();
 * </pre>
 *
 * @version 1.7
 */
public class GeometricShapeFactory
{
  private GeometryFactory geomFact;
  private Dimensions dim = new Dimensions();
  private int nPts = 100;

  /**
   * Create a shape factory which will create shapes using the default
   * {@link GeometryFactory}.
   */
  public GeometricShapeFactory()
  {
    this(new GeometryFactory());
  }

  /**
   * Create a shape factory which will create shapes using the given
   * {@link GeometryFactory}.
   *
   * @param geomFact the factory to use
   */
  public GeometricShapeFactory(GeometryFactory geomFact)
  {
    this.geomFact = geomFact;
  }

  /**
   * Sets the location of the shape by specifying the base coordinate
   * (which in most cases is the
   * lower left point of the envelope containing the shape).
   *
   * @param base the base coordinate of the shape
   */
  public void setBase(Coordinate base)  {  dim.setBase(base);    }
  /**
   * Sets the location of the shape by specifying the centre of
   * the shape's bounding box
   *
   * @param centre the centre coordinate of the shape
   */
  public void setCentre(Coordinate centre)  {  dim.setCentre(centre);    }

  /**
   * Sets the total number of points in the created {@link Geometry}.
   * The created geometry will have no more than this number of points,
   * unless more are needed to create a valid geometry.
   */
  public void setNumPoints(int nPts) { this.nPts = nPts; }

  /**
   * Sets the size of the extent of the shape in both x and y directions.
   *
   * @param size the size of the shape's extent
   */
  public void setSize(double size) { dim.setSize(size); }

  /**
   * Sets the width of the shape.
   *
   * @param width the width of the shape
   */
  public void setWidth(double width) { dim.setWidth(width); }

  /**
   * Sets the height of the shape.
   *
   * @param height the height of the shape
   */
  public void setHeight(double height) { dim.setHeight(height); }

  /**
   * Creates a rectangular {@link Polygon}.
   *
   * @return a rectangular Polygon
   *
   */
  public Polygon createRectangle()
  {
    int i;
    int ipt = 0;
    int nSide = nPts / 4;
    if (nSide < 1) nSide = 1;
    double XsegLen = dim.getEnvelope().getWidth() / nSide;
    double YsegLen = dim.getEnvelope().getHeight() / nSide;

    Coordinate[] pts = new Coordinate[4 * nSide + 1];
    Envelope env = dim.getEnvelope();

    double maxx = env.getMinX() + nSide * XsegLen;
    double maxy = env.getMinY() + nSide * XsegLen;

    for (i = 0; i < nSide; i++) {
      double x = env.getMinX() + i * XsegLen;
      double y = env.getMinY();
      pts[ipt++] = new Coordinate(x, y);
    }
    for (i = 0; i < nSide; i++) {
      double x = env.getMaxX();
      double y = env.getMinY() + i * YsegLen;
      pts[ipt++] = new Coordinate(x, y);
    }
    for (i = 0; i < nSide; i++) {
      double x = env.getMaxX() - i * XsegLen;
      double y = env.getMaxY();
      pts[ipt++] = new Coordinate(x, y);
    }
    for (i = 0; i < nSide; i++) {
      double x = env.getMinX();
      double y = env.getMaxY() - i * YsegLen;
      pts[ipt++] = new Coordinate(x, y);
    }
    pts[ipt++] = new Coordinate(pts[0]);

    LinearRing ring = geomFact.createLinearRing(pts);
    Polygon poly = geomFact.createPolygon(ring, null);
    return poly;
  }

  /**
   * Creates a circular {@link Polygon}.
   *
   * @return a circle
   */
  public Polygon createCircle()
  {

    Envelope env = dim.getEnvelope();
    double xRadius = env.getWidth() / 2.0;
    double yRadius = env.getHeight() / 2.0;

    double centreX = env.getMinX() + xRadius;
    double centreY = env.getMinY() + yRadius;

    Coordinate[] pts = new Coordinate[nPts + 1];
    int iPt = 0;
    for (int i = 0; i < nPts; i++) {
        double ang = i * (2 * Math.PI / nPts);
        double x = xRadius * Math.cos(ang) + centreX;
        double y = yRadius * Math.sin(ang) + centreY;
        Coordinate pt = new Coordinate(x, y);
        pts[iPt++] = pt;
    }
    pts[iPt] = pts[0];

    LinearRing ring = geomFact.createLinearRing(pts);
    Polygon poly = geomFact.createPolygon(ring, null);
    return poly;
  }

   /**
    * Creates a elliptical arc, as a LineString.
    *
    * @return an elliptical arc
    */
  public LineString createArc(
     double startAng,
     double endAng)
  {
    Envelope env = dim.getEnvelope();
    double xRadius = env.getWidth() / 2.0;
    double yRadius = env.getHeight() / 2.0;

    double centreX = env.getMinX() + xRadius;
    double centreY = env.getMinY() + yRadius;

     double angSize = (endAng - startAng);
     if (angSize <= 0.0 || angSize > 2 * Math.PI)
       angSize = 2 * Math.PI;
     double angInc = angSize / nPts;

     Coordinate[] pts = new Coordinate[nPts];
     int iPt = 0;
     for (int i = 0; i < nPts; i++) {
         double ang = startAng + i * angInc;
         double x = xRadius * Math.cos(ang) + centreX;
         double y = yRadius * Math.sin(ang) + centreY;
         Coordinate pt = new Coordinate(x, y);
         geomFact.getPrecisionModel().makePrecise(pt);
         pts[iPt++] = pt;
     }
     LineString line = geomFact.createLineString(pts);
     return line;
   }

  private class Dimensions
  {
    public Coordinate base;
    public Coordinate centre;
    public double width;
    public double height;

    public void setBase(Coordinate base)  {  this.base = base;    }
    public void setCentre(Coordinate centre)  {  this.centre = centre;    }
    public void setSize(double size)
    {
      height = size;
      width = size;
    }

    public void setWidth(double width) { this.width = width; }
    public void setHeight(double height) { this.height = height; }

    public Envelope getEnvelope() {
      if (base != null) {
        return new Envelope(base.x, base.x + width, base.y, base.y + height);
      }
      if (centre != null) {
        return new Envelope(centre.x - width/2, centre.x + width/2,
                            centre.y - height/2, centre.y + height/2);
      }
      return new Envelope(0, width, 0, height);
    }
  }
}
