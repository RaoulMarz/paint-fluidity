package com.codingcrafters.gameobjects

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
//import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.{Label, TextButton}

/** Created when program is launched; manages the screens that appear during the
  * game.
  */
object BaseGame {

  /** Stores reference to game; used when calling setActiveScreen method.
    */
  var game: BaseGame = null
  var defaultLabelStyle: Label.LabelStyle = null // BitmapFont + Color
  var titleLabelStyle: Label.LabelStyle = null
  var dialogueLabelStyle: Label.LabelStyle = null
  var infoLabelStyle: Label.LabelStyle = null

  var textButtonStyle: TextButton.TextButtonStyle =
    null // NPD + BitmapFont + Color

  /** Used to switch screens while game is running. Method is static to simplify
    * usage.
    */
  def setActiveScreen(s: BaseScreen): Unit = {
    game.setScreen(s)
  }
}

abstract class BaseGame()

/** Called when game is initialized; stores global reference to game object.
  */
    extends Game {
  BaseGame.game = this

  /** Called when game is initialized, after Gdx.input and other objects have
    * been initialized.
    */
  override def create()
      : Unit = { // prepare for multiple classes/stages/actors to receive discrete input
    val im = new InputMultiplexer
    Gdx.input.setInputProcessor(im)

    var assetFontsPath: String = "fonts"
    var assetUXPath: String = "ux"
    // parameters for generating a custom bitmap font
    val fontGeneratorSans = new FreeTypeFontGenerator(
      Gdx.files.internal(assetFontsPath + "/OpenSans.ttf")
    )
    val fontGeneratorRocked = new FreeTypeFontGenerator(
      Gdx.files.internal(assetFontsPath + "/rocked.ttf")
    )
    val fontGeneratorJagged = new FreeTypeFontGenerator(
      Gdx.files.internal(assetFontsPath + "/JaggedRegular-g2AE.ttf")
    )
    val fontParameters = new FreeTypeFontGenerator.FreeTypeFontParameter
    fontParameters.size = 42
    fontParameters.color = Color.WHITE
    fontParameters.borderWidth = 2
    fontParameters.borderColor = Color.BLACK
    fontParameters.borderStraight = true
    fontParameters.minFilter = TextureFilter.Linear
    fontParameters.magFilter = TextureFilter.Linear

    val fontParametersInfo = new FreeTypeFontGenerator.FreeTypeFontParameter
    fontParametersInfo.size = 26
    fontParametersInfo.color = Color.YELLOW
    fontParametersInfo.borderWidth = 3
    fontParametersInfo.borderColor = Color.BLUE
    fontParametersInfo.borderStraight = true
    fontParametersInfo.minFilter = TextureFilter.Linear
    fontParametersInfo.magFilter = TextureFilter.Linear

    val fontParametersRocked1 = new FreeTypeFontGenerator.FreeTypeFontParameter
    fontParametersRocked1.size = 40
    fontParametersRocked1.color = Color.YELLOW
    fontParametersRocked1.borderWidth = 2
    fontParametersRocked1.borderColor = Color.CHARTREUSE
    fontParametersRocked1.borderStraight = true
    fontParametersRocked1.minFilter = TextureFilter.Linear
    fontParametersRocked1.magFilter = TextureFilter.Linear
    val fontParametersJagged1 = new FreeTypeFontGenerator.FreeTypeFontParameter
    fontParametersJagged1.size = 52
    fontParametersJagged1.color = Color.YELLOW
    fontParametersJagged1.borderWidth = 2
    fontParametersJagged1.borderColor = Color.OLIVE
    fontParametersJagged1.borderStraight = true
    fontParametersJagged1.minFilter = TextureFilter.Linear
    fontParametersJagged1.magFilter = TextureFilter.Linear
    val customFont = fontGeneratorSans.generateFont(fontParameters)
    val infoFont = fontGeneratorSans.generateFont(fontParametersInfo)
    val dialogueFont = fontGeneratorRocked.generateFont(fontParametersRocked1)
    val titleFont = fontGeneratorJagged.generateFont(fontParametersJagged1)
    BaseGame.defaultLabelStyle = new Label.LabelStyle
    BaseGame.defaultLabelStyle.font = customFont
    BaseGame.infoLabelStyle = new Label.LabelStyle
    BaseGame.infoLabelStyle.font = infoFont
    BaseGame.dialogueLabelStyle = new Label.LabelStyle
    BaseGame.dialogueLabelStyle.font = dialogueFont
    BaseGame.titleLabelStyle = new Label.LabelStyle
    BaseGame.titleLabelStyle.font = titleFont

    BaseGame.textButtonStyle = new TextButton.TextButtonStyle
    val buttonTex = new Texture(Gdx.files.internal(assetUXPath + "/button.png"))
    val buttonPatch = new NinePatch(buttonTex, 24, 24, 24, 24)
    BaseGame.textButtonStyle.up = new NinePatchDrawable(buttonPatch)
    BaseGame.textButtonStyle.font = customFont
    BaseGame.textButtonStyle.fontColor = Color.GRAY
  }

  def setActiveScreen(s: BaseScreen): Unit = {
    BaseGame.game.setScreen(s)
  }
}
