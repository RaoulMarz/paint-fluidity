package com.codingcrafters.gameobjects

import com.badlogic.gdx.scenes.scene2d.Stage

class AnglerFish(override val xP: Float, override val yP: Float, override val s: Stage) extends BaseActor(xP, yP, s) {
  private var assetGamePath = "game"
  //loadAnimationFromSheet(assetGamePath + "/anglerfish_tiles.png", 2, 2, 1.8f, true)
  loadAnimationFromSheetScaled(assetGamePath + "/anglerfish_tiles.png", 2, 2, 1.8f, true, 0.5f, 0.5f)

  override def act(dt: Float): Unit = {
    super.act(dt)
    if (isAnimationFinished) remove
  }
}