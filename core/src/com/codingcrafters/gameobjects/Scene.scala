package com.codingcrafters.gameobjects

import com.badlogic.gdx.scenes.scene2d.Actor
import java.util

class Scene() extends Actor {
  private var segmentList = new util.ArrayList[SceneSegment]
  private var index = -1

  def addSegment(segment: SceneSegment): Unit = {
    segmentList.add(segment)
  }

  def clearSegments(): Unit = {
    segmentList.clear()
  }

  def start(): Unit = {
    index = 0
    segmentList.get(index).start()
  }

  override def act(dt: Float): Unit = {
    if (isSegmentFinished && !isLastSegment) loadNextSegment()
  }

  def isSegmentFinished: Boolean = segmentList.get(index).isFinished

  def isLastSegment: Boolean = index >= segmentList.size - 1

  def loadNextSegment(): Unit = {
    if (isLastSegment) return
    segmentList.get(index).finish()
    index += 1
    segmentList.get(index).start()
  }

  def isSceneFinished: Boolean = isLastSegment && isSegmentFinished
}