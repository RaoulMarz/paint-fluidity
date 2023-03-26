package com.codingcrafters.gameobjects

import java.util.Calendar
import com.codingcrafters.operational.Position

class TimePositionTracker {
  protected var setTime: Calendar = null
  protected var myPosition: Position = null

  def createFromCurrentTime(xPos : Float, yPos : Float): Unit = {
    setTime = Calendar.getInstance()
    Position.apply(xPos, yPos)
  }

}