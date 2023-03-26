package com.codingcrafters.gameobjects

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align


/**
 * Convenience class for creating custom Action objects
 * for use with SceneSegment and Scene classes.
 */
object SceneActions {
  def setText(s: String) = new SetTextAction(s)

  def pause: Action = Actions.forever(Actions.delay(1))

  def moveToScreenLeft(duration: Float): Action = Actions.moveToAligned(0, 0, Align.bottomLeft, duration)

  def moveToScreenRight(duration: Float): Action = Actions.moveToAligned(BaseActor.getWorldBounds.width, 0, Align.bottomRight, duration)

  def moveToScreenCenter(duration: Float, heightOffset : Float = 0.0f): Action = Actions.moveToAligned(BaseActor.getWorldBounds.width / 2, 0, Align.bottom, duration)

  def moveToOutsideLeft(duration: Float): Action = Actions.moveToAligned(0, 0, Align.bottomRight, duration)

  def moveToOutsideRight(duration: Float): Action = Actions.moveToAligned(BaseActor.getWorldBounds.width, 0, Align.bottomLeft, duration)
}

class SceneActions extends Actions {}