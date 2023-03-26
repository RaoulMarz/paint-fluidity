package com.codingcrafters.gameobjects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.graphics.{Color, Texture}
import com.badlogic.gdx.utils.Align
import com.codingcrafters.gameobjects.EnumerationDialogType.EnumerationDialogType

class DialogBox(override val xP: Float, override val yP: Float, override val s: Stage, val iconDecoration : String = "", val dialogType: EnumerationDialogType = EnumerationDialogType.DIALOG_TYPE_DEFAULT) extends BaseActor(xP, yP, s) {
  var dialogIconDecoration: Image = null
  var assetUXPath: String = "ux"
  var dialogTexture: String = "/dialog-translucent.png"
  if (dialogType == EnumerationDialogType.DIALOG_TYPE_TITLE)
    dialogTexture = "/dialog-jagged-title.png"
  if (dialogType == EnumerationDialogType.DIALOG_TYPE_INFO)
    dialogTexture = "/dialog-info-frame.png"
  loadTexture(assetUXPath + dialogTexture)
  var dialogLabel = new Label(" ", BaseGame.dialogueLabelStyle)
  if (dialogType == EnumerationDialogType.DIALOG_TYPE_TITLE)
    dialogLabel = new Label(" ", BaseGame.titleLabelStyle)
  if (dialogType == EnumerationDialogType.DIALOG_TYPE_INFO)
    dialogLabel = new Label(" ", BaseGame.infoLabelStyle)
  private val padding = 32
  dialogLabel.setWrap(true)
  dialogLabel.setAlignment(Align.topLeft)
  dialogLabel.setPosition(padding.toFloat, padding.toFloat)
  this.setDialogSize(getWidth, getHeight)
  this.addActor(dialogLabel)
  if (iconDecoration.length > 0) {
    val file1 = assetUXPath + "/" + iconDecoration
    val iconTexture = new Texture(Gdx.files.internal(file1))
    dialogIconDecoration = new Image(iconTexture)
    if (dialogIconDecoration != null) {
      dialogIconDecoration.setPosition(1.0f, 1.0f)
      this.addActor(dialogIconDecoration)
    }
    Gdx.app.log("DialogBox", s"iconDecoration=$iconDecoration, iconTexture=$iconTexture")
  }

  def setAlignmentPadding(paddingLeft : Int = 0, paddingTop : Int = 0, iconTopOffset : Int = 4): Unit = {
    setDialogSize(getWidth, getHeight, paddingLeft, paddingTop, iconTopOffset)
  }

  def setDialogSize(width: Float, height: Float, paddingLeft : Int = 0, paddingTop : Int = 0, iconTopOffset : Int = 4): Unit = {
    this.setSize(width, height)
    if (dialogIconDecoration != null) {
      val iconVerticalOffset = (height / 1.155f) - (iconTopOffset + (dialogIconDecoration.getImageHeight * 2.0f))
      Gdx.app.log("DialogBox", s"Icon.y=$iconVerticalOffset")
      dialogIconDecoration.setPosition(2.0f, iconVerticalOffset)
    }
    var pdL = paddingLeft
    var pdT = paddingTop
    if (pdL <= 0)
      pdL = padding
    if (pdT <= 0)
      pdT = padding
    dialogLabel.setWidth(width - 2 * pdL)
    dialogLabel.setHeight(height - 2 * pdT)
    dialogLabel.setPosition(pdL.toFloat, pdT.toFloat)
  }

  def setText(text: String): Unit = {
    dialogLabel.setText(text)
  }

  def setFontScale(scale: Float): Unit = {
    dialogLabel.setFontScale(scale)
  }

  def setFontColor(color: Color): Unit = {
    dialogLabel.setColor(color)
  }

  def setBackgroundColor(color: Color): Unit = {
    this.setColor(color)
  }

  def alignTopLeft(): Unit = {
    dialogLabel.setAlignment(Align.topLeft)
  }

  def alignCenter(): Unit = {
    dialogLabel.setAlignment(Align.center)
  }
}