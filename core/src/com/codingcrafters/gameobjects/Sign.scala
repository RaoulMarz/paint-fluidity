package com.codingcrafters.gameobjects

import com.badlogic.gdx.scenes.scene2d.Stage
import com.codingcrafters.gameobjects.EnumerationSignType.EnumerationSignType

class Sign(override val xP: Float, override val yP: Float, override val s: Stage) extends BaseActor(xP, yP, s) {
  private var assetGamePath = "game"
  loadTexture(assetGamePath + "/sign.png")
  var text : String = " "
  var signType = EnumerationSignType.SIGN_TYPE_BASIC
  var viewing = false

  def setText(t: String, signTypeValue : EnumerationSignType = EnumerationSignType.SIGN_TYPE_BASIC): Unit = {
    text = t
    signType = signTypeValue
  }

  def getText: String = text

  def setViewing(v: Boolean): Unit = {
    viewing = v
  }

  def isViewing: Boolean = viewing
}