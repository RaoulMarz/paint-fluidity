package com.codingcrafters.operational

import scala.collection.mutable.ListBuffer

object GeometryUtils {

  def euclideanDistance(aX: Float, aY: Float, bX: Float, bY: Float): Float = {
    Math.sqrt(
      Math.pow(aX - bX, 2) + Math.pow(aY - bY, 2)
    ).toFloat
  }

  def euclideanDistanceBetweenPoints(a: Position, b: Position): Float = {
    return euclideanDistance(a.x, a.y, b.x, b.y)
  }

  def getInclinationAngle(gradient : Double) : Double = {
    return Math.atan(gradient)
  }

  def getComponentDifference(a: Position, b: Position) : (Float, Float) = {
    return (b.x - a.x, b.y - a.y)
  }

  def withinTolerance(value : Float, target : Float, tolerance : Float): Boolean = {
    return (value >= (target - tolerance)) && (value <= (target + tolerance))
  }

  def findLineMidpoint(a: Position, b: Position) : Position = {
    return new Position( ((a.x - b.x) / 2.0f), ( (a.y - b.y) / 2.0f))
  }

  def findPolygonMidpoint(listPoints : List[Position]) : Position = {
    if (listPoints.isEmpty)
      return Position(0, 0)
    else {
      val numPoints = listPoints.length
      var xCoords = 0.0f
      var yCoords = 0.0f
      for (aPoint <- listPoints) {
        xCoords += aPoint.x
        yCoords += aPoint.y
      }
      return Position(xCoords / numPoints, yCoords / numPoints)
    }
  }

  def getExtendedPoint(sourceCoordinate : Position, targetCoordinate : Position, lineDistance : Int) : Position = {
    val gradient = getLineGradient(sourceCoordinate, targetCoordinate)
    if (gradient != Double.NaN) {
      if (gradient == 0.0) {
        //Get a line on the x-axis
        var offset = lineDistance
        if ((targetCoordinate.x - sourceCoordinate.x) <= 0)
          offset = -lineDistance
        return Position(sourceCoordinate.x + offset, sourceCoordinate.y)
      } else {
        val endDistance = euclideanDistanceBetweenPoints(sourceCoordinate, targetCoordinate)
        val normalVector : (Double, Double) = ((targetCoordinate.x - sourceCoordinate.x.toDouble) / endDistance,
          (targetCoordinate.y - sourceCoordinate.y.toDouble) / endDistance)
        val extendVector : Position = Position((lineDistance * normalVector._1).toFloat, (lineDistance * normalVector._2).toFloat)
        return Position(sourceCoordinate.x + extendVector.x, sourceCoordinate.y + extendVector.y)
      }
    } else {
      var offset = lineDistance
      if ((targetCoordinate.y - sourceCoordinate.y) <= 0)
        offset = -lineDistance
      return Position(sourceCoordinate.x, sourceCoordinate.y + offset)
    }
    return Position(0, 0)
  }

  def ellipsoidHitTest(center: Position, radiusX : Int, radiusY : Int, checkCoordinate : Position) : Int = {
    val p = (Math.pow(checkCoordinate.x - center.x, 2).toInt / Math.pow(radiusX, 2).toInt) + (Math.pow(checkCoordinate.y - center.y, 2).toInt / Math.pow(radiusY, 2).toInt)
    return p
  }

  def coordinatesListContains(coordinatesList: List[Position], coordinate: Position) : Boolean = {
    if (coordinatesList == null)
      return false
    else {
      val findCoordinate = coordinatesList.find(coord => coord == coordinate)
      return (findCoordinate != None)
    }
  }

  def getLineGradient(sourceCoordinate : Position, targetCoordinate : Position) : Double = {
    var res : Double = 0;
    if ( (sourceCoordinate != targetCoordinate) && (sourceCoordinate.x != targetCoordinate.x) ) {
      res = (sourceCoordinate.y - targetCoordinate.y) / (sourceCoordinate.x - targetCoordinate.x)
    } else {
      if (sourceCoordinate.x == targetCoordinate.x)
        return Double.NaN
    }
    return res
  }

  def ellipsoidAreaCoordinates(center: Position, radiusX : Float, radiusY : Float, width : Int, height : Int) : List[Position] = {
    val cells: ListBuffer[Position] = ListBuffer.empty[Position]
    val startX = Math.max(1, center.x - radiusX)
    val startY = Math.max(1, center.y - radiusY)
    val endX = Math.min(width.toFloat, center.x + radiusX)
    val endY = Math.min(width.toFloat, center.y + radiusY)
    /*
    for (
      i <- Range(startX, endX);
      j <- Range(startY, endY)
    ) {
      if (ellipsoidHitTest(center, radiusX, radiusY, Position(i, j)) <= 1)
        cells += Position(i, j)
    }
     */
    cells.toList
  }

}
