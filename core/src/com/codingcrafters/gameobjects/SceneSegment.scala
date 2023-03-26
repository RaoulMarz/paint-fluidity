package com.codingcrafters.gameobjects

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Action

class SceneSegment(var actor: Actor, var action: Action) {
  def start(): Unit = {
    actor.clearActions()
    actor.addAction(action)
  }

  def isFinished: Boolean = actor.getActions.size == 0

  /**
   * End this segment early
   */
  def finish(): Unit = { // simulate 100000 seconds elapsed time to complete in-progress action
    if (actor.hasActions) actor.getActions.first.act(100000)
    actor.clearActions()
  }
}