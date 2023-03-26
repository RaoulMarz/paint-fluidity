package com.codingcrafters.gameobjects

import scala.collection.mutable.ListBuffer

class TimePositionTrackerList(limit : Int) {
  protected var timePosTrackList : ListBuffer[TimePositionTracker] = ListBuffer[TimePositionTracker]()

  def clear(): Unit = {
    timePosTrackList.remove(0, timePosTrackList.length - 1)
  }

  def add(timePositionTracker: TimePositionTracker) = {
    if (timePosTrackList.length >= limit) {
      removeEntriesOnLimit(limit / 2)
    }
    timePosTrackList += timePositionTracker
  }

  def removeEntriesOnLimit(numToRemove : Int) = {
    if ( (timePosTrackList != null) && (numToRemove > 0) ) {
      timePosTrackList.drop(numToRemove)
    }
  }
}