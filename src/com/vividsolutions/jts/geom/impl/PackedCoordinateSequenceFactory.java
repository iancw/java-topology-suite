package com.vividsolutions.jts.geom.impl;

import com.vividsolutions.jts.geom.*;

/**
 * Builds packed array coordinate sequences. The array data type can be either
 * double or float, and defaults to float.
 */
public class PackedCoordinateSequenceFactory implements
    CoordinateSequenceFactory
{
  public static final int DOUBLE = 0;
  public static final int FLOAT = 1;

  public static final PackedCoordinateSequenceFactory DOUBLE_FACTORY =
      new PackedCoordinateSequenceFactory(DOUBLE);
  public static final PackedCoordinateSequenceFactory FLOAT_FACTORY =
      new PackedCoordinateSequenceFactory(FLOAT);

  private int type = DOUBLE;
  private int dimension = 3;

  /**
   * Creates a new PackedCoordinateSequenceFactory
   * of type DOUBLE.
   */
  public PackedCoordinateSequenceFactory()
  {
    this(DOUBLE);
  }

  /**
   * Creates a new PackedCoordinateSequenceFactory
   * of the given type.
   * Acceptable type values are
   * {@linkplain PackedCoordinateSequenceFactory#Float}or
   * {@linkplain PackedCoordinateSequenceFactory#Double}
   */
  public PackedCoordinateSequenceFactory(int type)
  {
    this(type, 3);
  }
  /**
   * Creates a new PackedCoordinateSequenceFactory
   * of the given type.
   * Acceptable type values are
   * {@linkplain PackedCoordinateSequenceFactory#FLOAT}or
   * {@linkplain PackedCoordinateSequenceFactory#DOUBLE}
   */
  public PackedCoordinateSequenceFactory(int type, int dimension)
  {
    setType(type);
    setDimension(dimension);
  }

  /**
   * Returns the type of packed coordinate sequences this factory builds, either
   * {@linkplain PackedCoordinateSequenceFactory#Float} or
   * {@linkplain PackedCoordinateSequenceFactory#Double}
   */
  public int getType() {
    return type;
  }

  /**
   * Sets the type of packed coordinate sequences this factory builds,
   * acceptable values are {@linkplain PackedCoordinateSequenceFactory#Float}or
   * {@linkplain PackedCoordinateSequenceFactory#Double}
   */
  public void setType(int type) {
    if (type != DOUBLE && type != FLOAT)
      throw new IllegalArgumentException("Unknown type " + type);
    this.type = type;
  }


  public int getDimension() { return dimension; }

  public void setDimension(int dimension) { this.dimension = dimension; }

  /**
   * @see com.vividsolutions.jts.geom.CoordinateSequenceFactory#create(com.vividsolutions.jts.geom.Coordinate[])
   */
  public CoordinateSequence create(Coordinate[] coordinates) {
    if (type == DOUBLE) {
      return new PackedCoordinateSequence.Double(coordinates, dimension);
    } else {
      return new PackedCoordinateSequence.Float(coordinates, dimension);
    }
  }

  /**
   * @see com.vividsolutions.jts.geom.CoordinateSequenceFactory#create(com.vividsolutions.jts.geom.CoordinateSequence)
   */
  public CoordinateSequence create(CoordinateSequence coordSeq) {
    if (type == DOUBLE) {
      return new PackedCoordinateSequence.Double(coordSeq.toCoordinateArray(), dimension);
    } else {
      return new PackedCoordinateSequence.Float(coordSeq.toCoordinateArray(), dimension);
    }
  }

  /**
   * @see com.vividsolutions.jts.geom.CoordinateSequenceFactory#create(double[],
   *      int)
   */
  public CoordinateSequence create(double[] packedCoordinates, int dimension) {
    if (type == DOUBLE) {
      return new PackedCoordinateSequence.Double(packedCoordinates, dimension);
    } else {
      return new PackedCoordinateSequence.Float(packedCoordinates, dimension);
    }
  }

  /**
   * @see com.vividsolutions.jts.geom.CoordinateSequenceFactory#create(float[],
   *      int)
   */
  public CoordinateSequence create(float[] packedCoordinates, int dimension) {
    if (type == DOUBLE) {
      return new PackedCoordinateSequence.Double(packedCoordinates, dimension);
    } else {
      return new PackedCoordinateSequence.Float(packedCoordinates, dimension);
    }
  }

  /**
   * @see com.vividsolutions.jts.geom.CoordinateSequenceFactory#create(int, int)
   */
  public CoordinateSequence create(int size, int dimension) {
    if (type == DOUBLE) {
      return new PackedCoordinateSequence.Double(size, dimension);
    } else {
      return new PackedCoordinateSequence.Float(size, dimension);
    }
  }
}