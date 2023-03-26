package com.codingcrafters.operational

import scala.util.Random

object RandomAssistFunctions {

  def createRandomSequence(low: Int, high : Int, count : Int) : List[Int] = {
    var res : List[Int] = List()
    var accumulate : Int = 0
    var realSum : Int = 0
    val rndGenerate = new scala.util.Random
    while (accumulate <= count) {
      val randomNum = rndGenerate.nextInt(high - low) + low
      accumulate += randomNum
      if (accumulate <= count) {
        res = randomNum :: res
        realSum += randomNum
      }
    }
    if (realSum < count) {
      res = (count - realSum) :: res
    }
    return res
  }

  def getRandomNumber() : Double = {
    val rndGenerate = new scala.util.Random
    return rndGenerate.nextDouble()
  }

  def getIntRandomNumber(top : Int) : Int = {
    return (getRandomNumber() * top).toInt
  }

  def getFloatRandomRange(min : Float, max : Float): Float = {
    var randVal = Random.nextFloat()
    randVal = (randVal * (max - min)) + min
    randVal
  }

  def createBrownianMotionPath(volatility : Double, startCoordinate : Position, endCoordinate : Position) : List[(Int, Int)] = {
    var res : List[(Int, Int)] = List()
    if ( (startCoordinate.x >= 0) && (startCoordinate.y >= 0) && (endCoordinate != startCoordinate) ) {
      val gradient = GeometryUtils.getLineGradient(startCoordinate, endCoordinate)
      if (gradient != Double.NaN) {
        val inclineAngle = GeometryUtils.getInclinationAngle(-gradient)
        println(s"createBrownianMotionPath(),gradient=$gradient,angle=$inclineAngle")
        //Determine the expected whole numbers to traverse on the y axis, for every whole increment of the x-axis, given the angle or gradient
        /*
        for (xs <- Range(startCoordinate.x, endCoordinate.x) ) {

        }
         */
      }
    }
    return res
  }
}