package com.codingcrafters.gameobjects

object DrawAnimationUtility {

  def showActor(imageActor: BaseActor, visible: Boolean) = {
    var assetGamePath : String = ""

    if (imageActor != null)
      imageActor.setVisible(visible)
  }

  def setActorImageProperties(imageActor: BaseActor, assetPath : String, textureRes: String, opacity: Float, xp: Float, yp: Float) = {
    if (imageActor != null) {
      imageActor.loadTexture(assetPath + textureRes)
      imageActor.setOpacity(opacity)
      imageActor.setPosition(xp, yp)
    }
  }

  def setActorImageExtendedProperties(imageActor: BaseActor, assetPath : String, textureRes: String, opacity: Float, xp: Float, yp: Float, scaleX: Float, scaleY: Float) = {
    if (imageActor != null) {
      imageActor.loadTexture(assetPath + textureRes)
      imageActor.setOpacity(opacity)
      imageActor.setScale(scaleX, scaleY)
      imageActor.setPosition(xp, yp)
    }
  }

  def setActorPositionAndScale(imageActor: BaseActor, xp: Float, yp: Float, scaleX: Float, scaleY: Float): Unit = {
    if (imageActor != null) {
      imageActor.setPosition(xp, yp)
      imageActor.setScale(scaleX, scaleY)
    }
  }

}
