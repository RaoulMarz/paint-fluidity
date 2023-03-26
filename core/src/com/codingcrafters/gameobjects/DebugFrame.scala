package com.codingcrafters.gameobjects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.graphics.{Color, Texture}
import com.badlogic.gdx.utils.Align
import com.codingcrafters.gameobjects.EnumerationDialogType.EnumerationDialogType

class DebugFrame(override val xP: Float, override val yP: Float, override val s: Stage,
                 override val iconDecoration: String = "",
                 override val dialogType: EnumerationDialogType = EnumerationDialogType.DIALOG_TYPE_DEFAULT) extends DialogBox(xP, yP, s, iconDecoration, dialogType) {

}