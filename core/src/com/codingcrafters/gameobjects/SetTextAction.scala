package com.codingcrafters.gameobjects

import com.badlogic.gdx.scenes.scene2d.Action


/**
 * Designed for use in concert with
 * the DialogBox, SceneSegment, and Scene classes.
 */
class SetTextAction(var textToDisplay: String) extends Action {
  override def act(dt: Float): Boolean = {
    val db = target.asInstanceOf[DialogBox]
    db.setText(textToDisplay)
    true // action completed

  }
}