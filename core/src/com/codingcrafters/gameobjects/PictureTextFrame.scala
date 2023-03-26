package com.codingcrafters.gameobjects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.graphics.{Color, Texture}
import com.badlogic.gdx.utils.Align

class PictureTextFrame(override val xP: Float, override val yP: Float, override val s: Stage, val iconDecoration : String, val styleTag : String) extends BaseActor(xP, yP, s)  {
  var assetUXPath: String = "ux"
  var dialogTexture: String = "/dialog-translucent.png"
  var dialogLabel = new Label(" ", BaseGame.dialogueLabelStyle)

  def setText(text: String): Unit = {
    dialogLabel.setText(text)
  }

  def setFontScale(scale: Float): Unit = {
    dialogLabel.setFontScale(scale)
  }

  def setFontColor(color: Color): Unit = {
    dialogLabel.setColor(color)
  }
}
