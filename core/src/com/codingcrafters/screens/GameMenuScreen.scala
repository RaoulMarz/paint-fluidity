package com.codingcrafters.screens

import java.util.Calendar

import com.badlogic.gdx.Gdx
import com.codingcrafters.gameobjects.EnumerationMenuItem.EnumerationMenuItem
import com.codingcrafters.gameobjects.{BaseActor, BaseGame, BaseScreen, DialogBox, DrawAnimationUtility, EnumerationDialogType, EnumerationMenuItem, SceneActions, SceneSegment}
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.graphics.Color
import com.codingcrafters.operational.EventRecord

class GameMenuScreen extends BaseScreen {
  Gdx.app.log("GameMenuScreen", "Construction, reset of variables")
  protected var mainCharacterImage: String = "/Mister_Shark_Game_Portrait_w380.png"
  protected var seagullChar: String = ""
  protected var backdropImage = "/ocean_below_splashintro_w800.png"
  protected var music_Intro: Music = null
  protected var background : BaseActor = null
  protected var dialogTitleBox : DialogBox = null
  protected var dialogInfoHelp : DialogBox = null
  protected var dialogBoxOptions: DialogBox = null
  protected var menuRefresh : Boolean = false

  private def initGameAssets() = {
    var backdropImage = "/ocean_below_splashintro_w800.png"
    addBagItem("game-help-text", "No mouse control\nPress TAB or Space or Enter to select, and the arrow keys to navigate")
    Gdx.app.log("GameMenuScreen", "initGameAssets() started")
    appWidth = Gdx.graphics.getWidth();
    appHeight = Gdx.graphics.getHeight();
    addBagItem("menu_selected_item", "none")

    background = new BaseActor(0, 0, mainStage)
    background.loadTexture(assetGamePath + backdropImage)
    background.setSize(appWidth.toFloat, appHeight.toFloat)
    background.setOpacity(1.0f)
    BaseActor.setWorldBounds(background)
    menuRefresh = false
    Gdx.app.log("GameMenuScreen", s"initGameAssets() background=$background")

    val music_Intro2 = Gdx.audio.newMusic(Gdx.files.internal(assetMusicPath + "/game_intro.ogg"))
    addMusicPlayer("menu-intro", music_Intro2)
  }

  private def configMusicPlayer(): Unit = {

  }

  override def initialize(): Unit = {
    super.initialize()
    clearBagItems()
    clearMusicPlayers()
    Gdx.app.log("GameMenuScreen", "initialize() started")

    initGameAssets()
    val prefDialogWidth = appWidth * 0.625;
    val prefDialogHeight = appHeight * 0.5;

    fxAnimations.clear()
    fxAnimations.addStaticAnimation("bubbleWall#1A", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("bubbleWall#2B", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("bubbleWall#3C", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("bubbleWall#4D", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("waveMotion#1A", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("waveMotion#2B", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("waveMotion#3C", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("seagullFlyer", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("waves#1", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("waves#2", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("menu-item#start", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("menu-item#options", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("menu-item#exit", new BaseActor(0, 0, mainStage))
    fxAnimations.addStaticAnimation("sub-title#donkey", new BaseActor(0, 0, mainStage))
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("bubbleWall#1A"), assetGamePath, "/bubble_screen#1.png", 0.32f, 200.0f, 150.0f )
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("bubbleWall#2B"), assetGamePath, "/bubble_screen#2.png", 0.35f, 500.0f, 250.0f)
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("bubbleWall#3C"), assetGamePath, "/bubble_screen#3.png", 0.35f, 500.0f, 250.0f )
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("bubbleWall#4D"), assetGamePath, "/bubble_screen#1.png", 0.35f, 500.0f, 250.0f )
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("seagullFlyer"), assetGamePath, "/straight_gull.png", 1.0f, 300.0f, 350.0f )
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("waveMotion#1A"), assetGamePath, "/style_wave#1.png", 0.32f, 10.0f, 50.0f )
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("waveMotion#2B"), assetGamePath, "/style_wave#2.png", 0.30f, 0.0f, 30.0f)
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("waveMotion#3C"), assetGamePath, "/style_wave#1.png", 0.34f, 0.0f, 65.0f )
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("menu-item#start"), assetGamePath, "/menu-item-start.png", 0.82f, 10.0f, 50.0f )
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("menu-item#options"), assetGamePath, "/menu-item-options.png", 0.82f, 0.0f, 30.0f)
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("menu-item#exit"), assetGamePath, "/menu-item-exit.png", 0.82f, 0.0f, 65.0f )
    DrawAnimationUtility.setActorImageProperties(fxAnimations.getAnimation("sub-title#donkey"), assetGamePath, "/subtitle_bites_on_ass.png", 0.89f, 360.0f, 255.0f )
    DrawAnimationUtility.setActorPositionAndScale(fxAnimations.getAnimation("seagullFlyer"), 300.0f, appHeight - 310.0f, 0.45f, 0.45f)
    DrawAnimationUtility.setActorPositionAndScale(fxAnimations.getAnimation("sub-title#donkey"), appWidth - 360.0f, appHeight - 220.0f, 0.6f, 0.6f)
    fxAnimations.getAnimation("sub-title#donkey").setVisible(false)
    drawGameMenuItems(false)

    var mainCharacterImage: String = "/Mister_Shark_Game_Portrait_w380.png"
    val sharkProwler = new BaseActor(0, 0, mainStage)
    sharkProwler.loadTexture(assetGamePath + mainCharacterImage)
    sharkProwler.scaleBy(0.7f, 0.7f)
    sharkProwler.setPosition(-sharkProwler.getWidth, appHeight / 2.2f)

    val music_Intro2 = getMusicPlayer("menu-intro")
    if (music_Intro2 != null) {
      music_Intro2.setLooping(true)
      music_Intro2.setVolume(0.84f)
      Gdx.app.log("GameMenuScreen", s"initialize() background music = ${music_Intro2}")
      music_Intro2.play()
    }

    val dialogBox = new DialogBox(0, 0, uiStage)
    dialogBox.setDialogSize(prefDialogWidth.toFloat, prefDialogHeight.toFloat)
    dialogBox.setBackgroundColor(new Color(0.6f, 0.6f, 0.8f, 1))
    dialogBox.setFontScale(0.75f)
    dialogBox.setVisible(false)
    uiTable.add(dialogBox).expandX.expandY.bottom

    Gdx.app.log("GameMenuScreen", s"adding scene = $scene to main stage")
    mainStage.addActor(scene)
    scene.addSegment(new SceneSegment(background, Actions.fadeIn(1)))
    scene.addSegment(new SceneSegment(sharkProwler, SceneActions.moveToOutsideRight(3.9f)))
    scene.addSegment(new SceneSegment(dialogBox, Actions.show))
    scene.addSegment(new SceneSegment(dialogBox, SceneActions.setText("Sharks rule the oceans... And we have to, because you need someone strong at the top")))
    //scene.addSegment(new SceneSegment(continueKey, Actions.show))
    scene.addSegment(new SceneSegment(background, SceneActions.pause))
    //scene.addSegment(new SceneSegment(continueKey, Actions.hide))
    scene.addSegment(new SceneSegment(dialogBox, SceneActions.setText("The top of the food chain, the totem pole of power ... My world is food ... I guess the world is food ")))
    //scene.addSegment(new SceneSegment(continueKey, Actions.show))
    scene.addSegment(new SceneSegment(background, SceneActions.pause))
    //scene.addSegment(new SceneSegment(continueKey, Actions.hide))
    scene.addSegment(new SceneSegment(dialogBox, Actions.hide))
    scene.addSegment(new SceneSegment(sharkProwler, SceneActions.moveToOutsideRight(1)))
    scene.addSegment(new SceneSegment(background, Actions.fadeOut(1)))
    scene.start()
  }

  def showGameTitle() = {
    if (dialogTitleBox == null) {
      dialogTitleBox = new DialogBox(120.0f, 75.0f, uiStage, "", EnumerationDialogType.DIALOG_TYPE_TITLE)
      dialogTitleBox.setAlignmentPadding(5, 6)
      Gdx.app.log("GameMenuScreen", s"showGameTitle(), dialogTitleBox = ${dialogTitleBox}")
      dialogTitleBox.setDialogSize(390, 160)
      dialogTitleBox.setBackgroundColor(new Color(0.4f, 0.46f, 0.82f, 1))
      dialogTitleBox.setFontScale(0.715f)
      dialogTitleBox.setPosition(appWidth - 420.0f, appHeight - 140.0f)
      dialogTitleBox.alignTopLeft()
      dialogTitleBox.setText(gameTitleText)
      dialogTitleBox.setVisible(true)
      fxAnimations.getAnimation("sub-title#donkey").setVisible(true)
      //uiTable.add(dialogTitleBox).top()
    }
  }

  def showGameHelpInfo(): Unit = {
    if (dialogInfoHelp == null) {
      dialogInfoHelp = new DialogBox(150.0f, 75.0f, uiStage, "info_symbol.png", EnumerationDialogType.DIALOG_TYPE_INFO)
      dialogInfoHelp.setAlignmentPadding(42, 42)

      Gdx.app.log("GameMenuScreen", s"showGameHelpInfo(), dialogInfoHelp = ${dialogInfoHelp}")
      dialogInfoHelp.setDialogSize(340, 145)
      dialogInfoHelp.setBackgroundColor(new Color(0.4f, 0.46f, 0.82f, 1))
      dialogInfoHelp.setFontScale(0.715f)
      dialogInfoHelp.setPosition(200.0f, appHeight - 75.0f)
      dialogInfoHelp.alignTopLeft()
      val gameHelpText = getBagItem("game-help-text")
      if (gameHelpText != null)
        dialogInfoHelp.setText(gameHelpText)
      dialogInfoHelp.setVisible(true)
      //uiTable.add(dialogInfoHelp).bottom()
    }
  }

  def updateGameMenu(/* currentChoice */ selected : EnumerationMenuItem) = {
    /* Sets and changes the menu components as they are selected */
    menuRefresh = true
    drawGameMenuItems(true, selected)
  }

  def setBaseActorImageProperties(imageActor : BaseActor, textureRes : String, opacity : Float, xp : Float, yp : Float) = {
    if (imageActor != null) {
      imageActor.loadTexture(assetGamePath + textureRes)
      imageActor.setOpacity(opacity)
      imageActor.setPosition(xp, yp)
    }
  }

  def nextMusicTrack(): Unit = {
    //if (music_Intro == null)
    var music_Intro2 = getMusicPlayer("menu-intro")
    if (music_Intro2 != null) {
      if (music_Intro2.isPlaying)
        music_Intro2.stop()
      music_Intro2.dispose()
      val nextSong = assetMusicPath + "/sharkwaters_mellow_chill.ogg"
      music_Intro2 = Gdx.audio.newMusic(Gdx.files.internal(nextSong))
      music_Intro2.setLooping(true)
      Gdx.app.log("GameMenuScreen", s"nextMusicTrack(), changing to = $nextSong")
      addMusicPlayer("menu-intro", music_Intro2)
      music_Intro2.play()
    } else {
      Gdx.app.log("GameMenuScreen", s"nextMusicTrack(), error,  music_Intro=$music_Intro2")
    }
  }

  def drawGameMenuItems(draw : Boolean, selected : EnumerationMenuItem = EnumerationMenuItem.MENU_ITEM_NONE): Unit = {
    //Gdx.app.log("GameMenuScreen", s"drawGameMenuItems(), draw = ${draw}")
    if ( (fxAnimations != null) && (!draw)) {
      val Actor1 = fxAnimations.getAnimation("menu-item#start")
      val Actor2 = fxAnimations.getAnimation("menu-item#options")
      val Actor3 = fxAnimations.getAnimation("menu-item#exit")
      if (Actor1 != null) {
        Actor1.setVisible(false)
        Actor2.setVisible(false)
        Actor3.setVisible(false)
      }
    }
    if ( (fxAnimations != null) && (menuRefresh) ) {
      menuRefresh = false
      val Actor1 = fxAnimations.getAnimation("menu-item#start")
      val Actor2 = fxAnimations.getAnimation("menu-item#options")
      val Actor3 = fxAnimations.getAnimation("menu-item#exit")
      if (Actor1 != null) {
        Actor1.setPosition(150.0f, appHeight - 165.0f)
        Actor1.setVisible(draw)
        if (selected == EnumerationMenuItem.MENU_ITEM_START) {
          Actor1.setOpacity(1.0f)
        } else
          Actor1.setOpacity(0.6f)
      }
      if (Actor2 != null) {
        Actor2.setPosition(150.0f, appHeight - 325.0f)
        Actor2.setVisible(draw)
        if (selected == EnumerationMenuItem.MENU_ITEM_OPTIONS) {
          Actor2.setOpacity(1.0f)
        } else
          Actor2.setOpacity(0.6f)
      }
      if (Actor3 != null) {
        Actor3.setPosition(150.0f, appHeight - 485.0f)
        Actor3.setVisible(draw)
        if (selected == EnumerationMenuItem.MENU_ITEM_EXIT) {
          Actor3.setOpacity(1.0f)
        } else
          Actor3.setOpacity(0.6f)
      }
    }
  }

  def drawFloatingBubbles(counter : Int) = {

    if (fxAnimations != null) {
      if ( (counter >= 10) && (counter <= 600) && (counter % 2 == 1) ) {
        //Gdx.app.log("GameMenuScreen", s"drawFloatingBubbles(), counter = ${counter}")
        val Actor1 = fxAnimations.getAnimation("bubbleWall#1A")
        val Actor2 = fxAnimations.getAnimation("bubbleWall#2B")
        val Actor3 = fxAnimations.getAnimation("bubbleWall#3C")
        val Actor4 = fxAnimations.getAnimation("bubbleWall#4D")
        if ( (Actor1 != null) && (counter % 3 == 1) )
          Actor1.setPosition(140.0f + (0.825f * counter) , appHeight - 80.0f - (1.0f * counter) )
        if (Actor2 != null)
          Actor2.setPosition(appWidth - (0.845f * counter) , appHeight - 90.0f - (1.15f * counter) )
        if (Actor3 != null)
          Actor3.setPosition(20.0f + (1.15f * counter) , appHeight - 50.0f - (1.35f * counter) )
        if (Actor4 != null)
          Actor4.setPosition(appWidth - 45.0f - (0.8125f * counter) , appHeight - 70.0f - (1.225f * counter) )
      }
    } else {
      Gdx.app.log("GameMenuScreen", "drawFloatingBubbles(), effectAnimations == null")
    }

  }

  override def update(dt: Float): Unit = {
    super.update(dt)
    if (mainStage != null) {
      val actorsList = mainStage.getActors()
      tickCounter += 1

      if (tickCounter == 1600) {
        nextMusicTrack()
      }
      try {
        if (tickCounter % 40 == 1)
          Gdx.app.log("GameMenuScreen", s"update() called, tickCounter = $tickCounter")
        if (menuMode) {
          var selectedItem : EnumerationMenuItem = EnumerationMenuItem.MENU_ITEM_NONE
          val menuSelectedValue = getBagItem("menu_selected_item")
          menuSelectedValue match {
            case "start" => selectedItem = EnumerationMenuItem.MENU_ITEM_START
            case "options" => selectedItem = EnumerationMenuItem.MENU_ITEM_OPTIONS
            case "exit" => selectedItem = EnumerationMenuItem.MENU_ITEM_EXIT
            case _ => selectedItem = EnumerationMenuItem.MENU_ITEM_NONE
          }
          updateGameMenu(selectedItem)
        }
        if ( (scene != null) && (scene.isSceneFinished) ) {
          if (!menuMode) {
            Gdx.app.log("GameMenuScreen", "update(), menuMode activated")
            val ActorGull = fxAnimations.getAnimation("seagullFlyer")
            if (ActorGull != null)
              ActorGull.setVisible(false)
            showGameTitle()
            showGameHelpInfo()
            menuMode = true
            addBagItem("menu_selected_item", "start")
          }
        }
        drawFloatingBubbles(tickCounter % 722)
      } catch {
        case ex: Exception => {
          Gdx.app.log("GameMenuScreen", s"update(), scene Exception = ${ex.getMessage}")
        }
      }
    }
  }

  private def selectPreviousMenuItem() = {
    val menuSelectedValue = getBagItem("menu_selected_item")
    var selectedString = "start"
    menuSelectedValue match {
      case "start" => selectedString = "start"
      case "options" => selectedString = "start"
      case "exit" => selectedString = "options"
      case _ => selectedString = "start"
    }
    menuRefresh = true
    addBagItem("menu_selected_item", selectedString)
  }

  private def selectNextMenuItem() = {
    val menuSelectedValue = getBagItem("menu_selected_item")
    var selectedString = "exit"
    menuSelectedValue match {
      case "start" => selectedString = "options"
      case "options" => selectedString = "exit"
      case "exit" => selectedString = "exit"
      case _ => selectedString = "exit"
    }
    menuRefresh = true
    addBagItem("menu_selected_item", selectedString)
  }

  private def startGamePlay() = {
    val music_Intro = getMusicPlayer("menu-intro")
    if (music_Intro != null) {
      music_Intro.stop()
      clearMusicPlayers()
    }
    //music_Intro.setLooping(true)
    BaseGame.setActiveScreen(new GamePlayWildWaters/*GameActionScreen()*/)
  }

  private def menuActionEvent(keyCode: Int): Unit = {
    if (menuMode) {
      val menuSelectedValue = getBagItem("menu_selected_item")
      menuSelectedValue match {
        case "start" => startGamePlay()
        case "options" => { addEventRecord("options-launch-window", new EventRecord(Calendar.getInstance()))
                            showOptions() }
        case "exit" => Gdx.app.exit()
        case _ => { }
      }
    }
  }

  private def showOptions() = {
    if (dialogBoxOptions == null) {
      dialogBoxOptions = new DialogBox(545.0f, 210.0f, uiStage, "options_symbol.png", EnumerationDialogType.DIALOG_TYPE_INFO)
      dialogBoxOptions.setAlignmentPadding(42, 42)
    }
    //val dialogBoxOptions = new DialogBox(0, 0, uiStage)
    //addIcon(10, 50)
    dialogBoxOptions.setDialogSize(480, 270)
    dialogBoxOptions.setBackgroundColor(new Color(0.6f, 0.6f, 0.8f, 1))
    dialogBoxOptions.setFontScale(0.85f)
    dialogBoxOptions.setVisible(true)
    dialogBoxOptions.setText("Options (WIP) - Minimal Settings")
    //uiTable.add(dialogBoxOptions).expandX.expandY.center()
  }

  override def keyDown(keyCode: Int): Boolean = {
    Gdx.app.log("GameMenuScreen", s"keyDown(), keyCode = $keyCode , scene = $scene")
    if ((scene != null) && (keyCode == Keys.SPACE || keyCode == Keys.TAB) /*&& continueKey.isVisible*/) {
      Gdx.app.log("GameMenuScreen", "loading next segment")
      scene.loadNextSegment()
    }
    if ((scene != null) && (keyCode == Keys.UP || keyCode == Keys.DOWN || keyCode == Keys.LEFT || keyCode == Keys.RIGHT) /*&& continueKey.isVisible*/) {
      Gdx.app.log("GameMenuScreen", "using arrow keys")
      if (menuMode) {
        if (keyCode == Keys.UP || keyCode == Keys.LEFT) {
          selectPreviousMenuItem()
        }
        if (keyCode == Keys.DOWN || keyCode == Keys.RIGHT) {
          selectNextMenuItem()
        }
      }
    }
    if ((scene != null) && (keyCode == Keys.ENTER || keyCode == Keys.SPACE || keyCode == Keys.TAB) ) {
      if (menuMode) {
        menuActionEvent(keyCode)
      }
    }
    false
  }
}