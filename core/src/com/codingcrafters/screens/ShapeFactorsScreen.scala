package com.codingcrafters.screens

import com.codingcrafters.gameobjects.{AnglerFish, BaseActor, BaseGame, BaseScreen, ColorPaintWheel, DialogBox, SceneActions, SceneSegment, Sign, VectorDrawComponent}
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch, TextureRegion}
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.{Game, Gdx, Input, Screen}
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera}
import com.badlogic.gdx.math.{MathUtils, Vector2}
import com.badlogic.gdx.physics.box2d.{Body, BodyDef, Box2DDebugRenderer, ChainShape, CircleShape, FixtureDef, PolygonShape, World}
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.codingcrafters.constants.Constants
import aurelienribon.bodyeditor.BodyEditorLoader
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Texture.TextureFilter

import java.time.{Duration, LocalDateTime, LocalTime}
import scala.math.*

class ShapeFactorsScreen extends BaseScreen {
  private val camera =
    new OrthographicCamera(
      Gdx.graphics.getWidth.toFloat,
      Gdx.graphics.getHeight.toFloat
    )
  camera.position.set(
    Gdx.graphics.getWidth.toFloat / 2,
    Gdx.graphics.getHeight.toFloat / 2,
    0
  )
  private var initCompleted: Boolean = false
  private val worldMetersWidth = Gdx.graphics.getWidth / Constants.PIXEL_PER_METER
  private val worldMetersHeight = Gdx.graphics.getHeight / Constants.PIXEL_PER_METER
  private var showPhysicsDebug : Boolean = false
  private val physicsDebugCam = new OrthographicCamera(worldMetersWidth, worldMetersHeight)
  appWidth = Gdx.graphics.getWidth
  appHeight = Gdx.graphics.getHeight
  private val worldBox = new World(new Vector2(0, -9.81f), true)
  private val debugRenderer : Box2DDebugRenderer = new Box2DDebugRenderer()
  private val batch: SpriteBatch = new SpriteBatch()
  private var instructionsSign : Option[Sign] = None //new Sign(400, (appHeight - 480).toFloat, mainStage)
  private var paintWheel : Option[ColorPaintWheel] = None //new ColorPaintWheel(50, 60, mainStage, 440f, 440.0f)
  private var compartmentVolumes: Option[scala.Array[Int]] = None
  private var compartmentFilledRatios: Option[scala.Array[Float]] = None
  private var compartmentColors: Option[scala.Array[Color]] = None
  private val backdropImage: String = "/spoon_worlds.png"
  private val numberPizelDust = 400
  private val PIXELDUST_RADIUS = 0.04f
  private val groundWorldHeight = 0.5f
  private var pixelDustModels : Option[scala.Array[Body]] = None
  private val numberCollectorJars = 6
  private val collectorJarModels: scala.Array[Option[Body]] = new scala.Array[Option[Body]](numberCollectorJars)
  for (ij <- 0 until numberCollectorJars) collectorJarModels.update(ij, None)

  // Balloons constraints
  private val BALLOON_WIDTH = 0.5f
  private val BALLOON_HEIGHT = 0.664f
  // To simplify we will consider the balloon as an ellipse (A = PI * semi-major axis * semi-minor axis)
  private val BALLOON_AREA = Pi.toFloat * BALLOON_WIDTH * 0.5f * BALLOON_HEIGHT * 0.5f
  private val numberBalloons = 4
  private var balloonInstance: Int = 0
  private val balloonModels: scala.Array[Option[Body]] = new scala.Array[Option[Body]](numberBalloons)
  for (ib <- 0 until numberBalloons) balloonModels.update(ib, None)
  private val airDensity = 0.01f
  private val balloonDensity = 0.0099999f
  private val balloonFriction = 0.90f
  private val balloonRestitution = 0.0f
  private val displacedMass: Float = BALLOON_AREA * airDensity
  private val buoyancyForce: Vector2 = new Vector2(0f, displacedMass * 9.8f)
  private val balloonFD: FixtureDef = new FixtureDef()

  private val COLLECTOR_JAR_WIDTH = 2.1f
  private var collectorJarModelOrigin: Vector2 = null
  private val jarFD: FixtureDef = new FixtureDef()
  private var jarBody: Body = null

  private var bottleTexture: Texture = null
  private var balloonTexture: Texture = null
  private var balloonModelOrigin: Vector2 = null
  private var balloonSpritesList: scala.Array[Option[Sprite]] = new scala.Array[Option[Sprite]](numberBalloons)
  for (ib <- 0 until numberBalloons) balloonSpritesList.update(ib, None)
  private var pixelDustTexture: Texture = null
  private var pixelDustSprites: scala.Array[Option[Sprite]] = new scala.Array[Option[Sprite]](numberPizelDust)
  for (ip <- 0 until numberPizelDust) pixelDustSprites.update(ip, None)
  private var jarCollectorSpritesList: scala.Array[Option[Sprite]] = new scala.Array[Option[Sprite]](numberCollectorJars)
  for (ib <- 0 until numberCollectorJars) jarCollectorSpritesList.update(ib, None)
  private var groundTexture: Texture = null
  private var groundSprite: Sprite = null

  private var referenceTime = LocalDateTime.now
  // val myVectDrawer = new VectorDrawComponent(250, (appHeight - 480).toFloat, mainStage)
  // val dialogBox = new DialogBox(0, 0, uiStage)
  camera.update()

  def initGameAssets(): Unit = {
    Gdx.app.log("ShapeFactorsScreen", "initGameAssets() started")
    appWidth = Gdx.graphics.getWidth
    appHeight = Gdx.graphics.getHeight
  }

  private def createGround(): Unit = {
    val halfGroundWidth = worldMetersWidth * 0.5f
    val halfGroundHeight = groundWorldHeight * 0.5f
    val worldBoundHeight = worldMetersHeight / 2.0f

    // Create a static body definition
    val groundBodyDef : BodyDef = new BodyDef()
    groundBodyDef.`type` = BodyType.StaticBody
    groundBodyDef.position.set(0f, -worldBoundHeight + halfGroundHeight/*groundWorldHeight*/)
    //groundBodyDef.position.set(halfGroundWidth * 0.5f, worldMetersHeight - halfGroundHeight)
    // Create a body from the definition and add it to the world
    val groundBody : Body = worldBox.createBody(groundBodyDef)
    // Create a rectangle shape which will fit the virtual_width and 1 meter high
    // (setAsBox takes half-width and half-height as arguments)
    val groundBox : PolygonShape = new PolygonShape()
    groundBox.setAsBox(halfGroundWidth, halfGroundHeight)
    // Create a fixture from our rectangle shape and add it to our ground body
    groundBody.createFixture(groundBox, 0.0f)
    // Free resources
    groundBox.dispose()
  }

  private def createFunnelChainShapes(objPlacement : Vector2, funnelWidth : Float = 0f): Unit = {
    val chainShape : ChainShape = new ChainShape()
    var chainVertices : scala.Array[Vector2] = null
    if (funnelWidth <= 0f) {
      chainVertices = scala.Array(
      new Vector2(.0f, .0f),
      new Vector2(1.0f, .0f), new Vector2(0.6f, 1.0f), new Vector2(0.4f, 1.0f)
    )
    } else {
      chainVertices = scala.Array(
        new Vector2(.0f, .0f),
        new Vector2(funnelWidth, .0f), new Vector2(0.6f * funnelWidth, funnelWidth), new Vector2(0.4f * funnelWidth, funnelWidth)
      )
    }
    chainShape.createLoop(chainVertices)
    val chainBodyDef : BodyDef = new BodyDef()
    chainBodyDef.`type` = BodyType.StaticBody
    chainBodyDef.position.set(objPlacement.x, objPlacement.y)
    val chainBody : Body = worldBox.createBody(chainBodyDef)
    chainBody.createFixture(chainShape, 0)
    chainShape.dispose()
  }

  private def createBalloon(): Unit = {
    balloonFD.density = balloonDensity
    balloonFD.friction = balloonFriction
    balloonFD.restitution = balloonRestitution
    val maxWidth = (worldMetersWidth - BALLOON_WIDTH * 0.5f)
    val maxHeight = (worldMetersHeight - BALLOON_HEIGHT * 0.5f)
    val minWidth = (0 + BALLOON_WIDTH * 0.5f)
    val minHeight = (1 + BALLOON_HEIGHT * 0.5f)
    val x = MathUtils.random(minWidth, maxWidth)
    val y = MathUtils.random(minHeight, maxHeight)
    val loader : BodyEditorLoader = new BodyEditorLoader(Gdx.files.internal("data/box2D/balloon.json"))
    // Create the balloon body definition and place it in within the world
    val bd : BodyDef = new BodyDef()
    bd.`type` = BodyType.DynamicBody
    bd.position.set(x, y)
    // Create the balloon body
    val balloonBody : Body = worldBox.createBody(bd)
    balloonBody.setUserData(false) // Set to true if it must be destroyed, false means active

    loader.attachFixture(balloonBody, "balloon", balloonFD, BALLOON_WIDTH)
    balloonModels.update(balloonInstance, Some(balloonBody))
    balloonInstance += 1
    if (balloonModelOrigin == null)
      balloonModelOrigin = loader.getOrigin("balloon", BALLOON_WIDTH).cpy()
  }

  private def createCollectorJar(jarPlacement : Vector2, jarIdIndex : Int = 0): Unit = {
    jarFD.density = 1
    jarFD.friction = 0.5f
    jarFD.restitution = 0.05f
    val loader: BodyEditorLoader = new BodyEditorLoader(Gdx.files.internal("data/box2D/ketchup-collect.json"))
    val bd: BodyDef = new BodyDef()
    bd.`type` = BodyType.StaticBody
    bd.position.set(jarPlacement.x, jarPlacement.y)
    // Create the collector jar body
    val newJarBody : Body = worldBox.createBody(bd)
    newJarBody.setUserData(false)

    loader.attachFixture(newJarBody, "catch-jar", jarFD, COLLECTOR_JAR_WIDTH)
    collectorJarModels.update(jarIdIndex, Some(newJarBody))
    if (collectorJarModelOrigin == null)
      collectorJarModelOrigin = loader.getOrigin("catch-jar", COLLECTOR_JAR_WIDTH).cpy()
  }

  private def createBlockStatic(blockPlacement : Vector2) : Unit = {
    val containerSize = new Vector2(worldMetersWidth * 0.35f, worldMetersHeight * 0.325f)
    val containerDef: BodyDef = new BodyDef()
    containerDef.`type` = BodyType.StaticBody
    val containerPosition = new Vector2(-(containerSize.x * 0.5f), (worldMetersHeight / 2.0f) - (containerSize.y * 0.5725f))
    containerDef.position.set(containerPosition.x, containerPosition.y)
    val container: Body = worldBox.createBody(containerDef)
    val containerShape: PolygonShape = new PolygonShape()
    containerShape.setAsBox(containerSize.x * 0.5f, containerSize.y * 0.5f) // in meters
    container.createFixture(containerShape, 0)
    containerShape.dispose()
  }

  private def createPhysicsSprites(): Unit = {
    bottleTexture = new Texture(Gdx.files.internal("data/box2D/ketchup-catcher-jar.png"))
    bottleTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear)

    balloonTexture = new Texture(Gdx.files.internal("data/box2D/balloon.png"))
    balloonTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear)
    //balloonSprite = new Sprite(balloonTexture)
    //balloonSprite.setSize(BALLOON_WIDTH, BALLOON_WIDTH * balloonSprite.getHeight / balloonSprite.getWidth)

    pixelDustTexture = new Texture(Gdx.files.internal("data/box2D/ball.png"))
    pixelDustTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear)

    groundTexture = new Texture(Gdx.files.internal("data/box2D/gritty-ground.png"))
    groundSprite = new Sprite(groundTexture)
    groundSprite.setSize(worldMetersWidth, groundWorldHeight)

    for (i <- 0 until numberPizelDust) {
      val spriteBall : Sprite = new Sprite(pixelDustTexture)
      spriteBall.setSize(PIXELDUST_RADIUS * 2, PIXELDUST_RADIUS * 2)
      spriteBall.setOrigin(PIXELDUST_RADIUS, PIXELDUST_RADIUS)
      pixelDustSprites.update(i, Some(spriteBall))
    }
    for (i <- 0 until numberBalloons) {
      val newBalloonSprite : Sprite = new Sprite(balloonTexture)
      newBalloonSprite.setSize(BALLOON_WIDTH, BALLOON_WIDTH * newBalloonSprite.getHeight / newBalloonSprite.getWidth)
      //newBalloonSprite.setOrigin(BALLOON_WIDTH, BALLOON_HEIGHT)
      balloonSpritesList.update(i, Some(newBalloonSprite))
    }
    for (i <- 0 until numberCollectorJars) {
      val newJarSprite : Sprite = new Sprite(bottleTexture)
      newJarSprite.setSize(COLLECTOR_JAR_WIDTH, COLLECTOR_JAR_WIDTH * newJarSprite.getHeight / newJarSprite.getWidth)
      jarCollectorSpritesList.update(i, Some(newJarSprite))
    }
  }

  private def setupFlowPhysicsObjects(): Unit = {
    if (worldBox == null)
      return ()

    val worldBoundHeight = worldMetersHeight / 2.0f

    resetPixelDust()
    createFunnelChainShapes(new Vector2(-2.5f, worldBoundHeight - (0.385f * worldMetersHeight)))
    createFunnelChainShapes(new Vector2(-(worldMetersWidth / 2f), -worldBoundHeight + groundWorldHeight))
    createFunnelChainShapes(new Vector2( (worldMetersWidth / 2f) - 0.8f, -worldBoundHeight + groundWorldHeight), 0.8f)
    createCollectorJar(new Vector2(-(worldMetersWidth / 2f) + 0.9f, -worldBoundHeight + groundWorldHeight), 0)
    createCollectorJar(new Vector2(-(worldMetersWidth / 2f) + 2.9f, -worldBoundHeight + groundWorldHeight), 1)
    createCollectorJar(new Vector2(-(worldMetersWidth / 2f) + 5.5f, -worldBoundHeight + groundWorldHeight + 0.4f), 2)
    createGround()
    for (ib <- 0 until numberBalloons) {
      createBalloon()
    }
  }

  private def setupColorWheel(): Unit = {
    if (paintWheel.isEmpty) {
      val wheelRadius : Float = 150f
      //(appWidth * 0.5f) - wheelRadius * 0.5f, appHeight - (wheelRadius * 2.1f)
      paintWheel = Some(new ColorPaintWheel((appWidth * 0.5f) - wheelRadius * 0.5f, appHeight - ((wheelRadius * 2.1f) + 400), mainStage, 320f, 320.0f, wheelRadius))
      compartmentVolumes = Some(new scala.Array[Int](5))
      compartmentFilledRatios = Some(new scala.Array[Float](5))
      compartmentColors = Some(new scala.Array[Color](5))
      compartmentVolumes.get.update(0, 15)
      compartmentVolumes.get.update(1, 20)
      compartmentVolumes.get.update(2, 15)
      compartmentVolumes.get.update(3, 25)
      compartmentVolumes.get.update(4, 25)
      compartmentColors.get.update(0, Color.FIREBRICK)
      compartmentColors.get.update(1, Color.BLUE)
      compartmentColors.get.update(2, Color.OLIVE)
      compartmentColors.get.update(3, Color.ORANGE)
      compartmentColors.get.update(4, Color.PURPLE)
    }
  }

  private def resetPixelDust() : Unit = {
    val containerSize = new Vector2(worldMetersWidth * 0.75f, worldMetersHeight * 0.15f)
    val containerPosition = new Vector2(-(containerSize.x * 0.5f), (worldMetersHeight / 2.0f) - (containerSize.y * 0.525f))

    val sandDef: BodyDef = new BodyDef()
    sandDef.`type` = BodyType.DynamicBody
    val sandShape: CircleShape = new CircleShape()
    sandShape.setRadius(PIXELDUST_RADIUS) // in meters

    pixelDustModels = Some(new scala.Array[Body](numberPizelDust))
    for (i <- 0 until numberPizelDust) {
      // Randomize the position of the sand particles inside the container
      val containerTopLeft = new Vector2(containerPosition.x - (containerSize.x * 0.5f), containerPosition.y - (containerSize.y * 0.5f))
      sandDef.position.set(MathUtils.random(containerTopLeft.x, containerTopLeft.x + containerSize.x),
        MathUtils.random(containerTopLeft.y, containerTopLeft.y + containerSize.y)) // in meters
      val sand: Body = worldBox.createBody(sandDef)
      //if (pixelDustModels.get.nonEmpty) --> dispose if necessary
      pixelDustModels.get.update(i, sand)
      sand.createFixture(sandShape, 1)
    }
    sandShape.dispose()
  }

  override def initialize(): Unit = {
    super.initialize()
    appWidth = Gdx.graphics.getWidth
    appHeight = Gdx.graphics.getHeight
    setupFlowPhysicsObjects()
    createPhysicsSprites()
    instructionsSign: Option[Sign]
    instructionsSign = Some(new Sign(-400, ( (appHeight / 2f) - 480), mainStage))
    setupColorWheel()
    clearBagItems()

    Gdx.app.log("Paint-Fluidity", s"initGameAssets(), appWidth=${appWidth}, appHeight=${appHeight}, instructionsSign=${instructionsSign}")
    paintWheel.get.updateRenderer(Color.FIREBRICK, Color.BLUE, 205.0f)
    /*
    /////////////////
    val myVectDrawer =
      new VectorDrawComponent(250, (appHeight - 480).toFloat, mainStage)
    val dialogBox = new DialogBox(0, 0, uiStage)
    /////////////////
    val prefDialogWidth = appWidth * 0.375
    val prefDialogHeight = appHeight * 0.35
    dialogBox.setDialogSize(prefDialogWidth.toFloat, prefDialogHeight.toFloat)
    dialogBox.setBackgroundColor(new Color(0.6f, 0.6f, 0.8f, 1))
    dialogBox.setFontScale(0.75f)
    dialogBox.setVisible(false)
    //uiTable.add(dialogBox).expandX.expandY.bottom
    */

    val buttonStyle = new ButtonStyle()
    val buttonTex = new Texture(Gdx.files.internal("ux/undo.png"))
    val buttonRegion = new TextureRegion(buttonTex)
    buttonStyle.up = new TextureRegionDrawable(buttonRegion)
    val restartButton = new Button(buttonStyle)
    restartButton.setColor(Color.CYAN)
    restartButton.setPosition(720, 520)
    uiTable.add(restartButton).expandX.expandY.bottom
    val devyFish = new AnglerFish(350, (appHeight - 480).toFloat, mainStage)
    initCompleted = true

    Gdx.app.log("ShapeFactorsScreen", s"adding scene = $scene to main stage")
    mainStage.addActor(scene)
    //scene.addSegment(new SceneSegment(dialogBox, Actions.show))
    scene.addSegment(new SceneSegment(instructionsSign.get, Actions.show))
    scene.addSegment(new SceneSegment(instructionsSign.get, Actions.fadeIn(3)))
    //scene.addSegment(new SceneSegment(paintWheel.get, Actions.show))
    //scene.addSegment(new SceneSegment(paintWheel.get, Actions.fadeIn(3)))
    //scene.addSegment(new SceneSegment(paintWheel.get, Actions.rotateBy(30.0f, 4.0f)

    scene.addSegment(
      new SceneSegment(devyFish, Actions.moveTo(900f, 980f, 2.5f))
    )
    /*
    scene.addSegment(
      new SceneSegment(paintWheel, Actions.moveTo(30f, 120f, 2.5f))
    )
    */

    scene.addSegment(
      new SceneSegment(instructionsSign.get, Actions.moveTo(20f, (appHeight * 0.5f) - 120, 3.0f))
    )
    //scene.addSegment(new SceneSegment(myVectDrawer, Actions.show))
    //scene.addSegment(
    //  new SceneSegment(myVectDrawer, Actions.moveTo(50f, 250f, 5.0f))
    //)
    scene.start()
  }

  private def renderPhysicsObjects() : Unit = {
    //val collectorJarPos : Vector2 = jarBody.getPosition.sub(collectorJarModelOrigin)
    //bottleSprite.setPosition(collectorJarPos.x, collectorJarPos.y)
    //bottleSprite.setOrigin(collectorJarModelOrigin.x, collectorJarModelOrigin.y)

    for (i <- 0 until numberPizelDust) {
      val ballPos = pixelDustModels.get(i).getPosition
      if ( (ballPos.x >= -worldMetersWidth) && (ballPos.x <= worldMetersWidth) &&
        (ballPos.y >= -worldMetersHeight) && (ballPos.y <= worldMetersHeight) && (pixelDustSprites(i).nonEmpty) ) {
        pixelDustSprites(i).get.setPosition(ballPos.x - pixelDustSprites(i).get.getWidth / 2, ballPos.y - pixelDustSprites(i).get.getHeight / 2)
        pixelDustSprites(i).get.setRotation(pixelDustModels.get(i).getAngle * MathUtils.radiansToDegrees)
      }
    }
    for (i <- 0 until numberBalloons) {
      if (balloonModels(i).nonEmpty) {
        val balloonPos = balloonModels(i).get.getPosition
        if ((balloonPos.x >= -worldMetersWidth) && (balloonPos.x <= worldMetersWidth) &&
          (balloonPos.y >= -worldMetersHeight) && (balloonPos.y <= worldMetersHeight) && (balloonSpritesList(i).nonEmpty)) {
          val balloonModelPos : Vector2 = balloonModels(i).get.getPosition.sub(balloonModelOrigin)
          balloonSpritesList(i).get.setPosition(balloonModelPos.x, balloonModelPos.y)
          //balloonSpritesList(i).get.setPosition(balloonPos.x - balloonSpritesList(i).get.getWidth / 2, balloonPos.y - balloonSpritesList(i).get.getHeight / 2)
          balloonSpritesList(i).get.setRotation(balloonModels(i).get.getAngle * MathUtils.radiansToDegrees)
        }
      }
    }
    for (i <- 0 until numberCollectorJars) {
      if (collectorJarModels(i).nonEmpty) {
        val jarPos = collectorJarModels(i).get.getPosition
        if ((jarPos.x >= -worldMetersWidth) && (jarPos.x <= worldMetersWidth) &&
          (jarPos.y >= -worldMetersHeight) && (jarPos.y <= worldMetersHeight) && (jarCollectorSpritesList(i).nonEmpty)) {
          val collectorJarPos : Vector2 = collectorJarModels(i).get.getPosition.sub(collectorJarModelOrigin)
          jarCollectorSpritesList(i).get.setPosition(collectorJarPos.x, collectorJarPos.y)
          jarCollectorSpritesList(i).get.setRotation(collectorJarModels(i).get.getAngle * MathUtils.radiansToDegrees)
        }
      }
    }

    groundSprite.setPosition(-worldMetersWidth / 2, (-worldMetersHeight / 2f))
    //groundSprite.setColor(Color.BLACK)
    batch.setProjectionMatrix(physicsDebugCam.combined)
    batch.begin()
    groundSprite.draw(batch)
    //bottleSprite.draw(batch)
    for (ix <- 0 until numberPizelDust) {
      if (pixelDustSprites(ix).nonEmpty)
        pixelDustSprites(ix).get.draw(batch)
    }
    for (ix <- 0 until numberBalloons) {
      if ( (balloonModels(ix).nonEmpty) && (balloonSpritesList(ix).nonEmpty) )
        balloonSpritesList(ix).get.draw(batch)
    }
    for (ix <- 0 until numberCollectorJars) {
      if ( (collectorJarModels(ix).nonEmpty) && (jarCollectorSpritesList(ix).nonEmpty) )
        jarCollectorSpritesList(ix).get.draw(batch)
    }
    batch.end()
  }

  private def exitScreen() : Unit = {
    Gdx.app.exit()
  }

  override def dispose(): Unit = {
    super.dispose()
    pixelDustTexture.dispose()
    balloonTexture.dispose()
    groundTexture.dispose()
    batch.dispose()
    debugRenderer.dispose()
    //batch.dispose
    worldBox.dispose()
  }

  override def render(dt: Float): Unit = {
    super.render(dt)
    if (!initCompleted)
      return ()
    val compareTime: LocalDateTime = LocalDateTime.now()
    val timeDifference: Long = scala.math.abs(Duration.between(referenceTime, compareTime).toMillis)
    if (timeDifference >= 33) {
      renderTickCounter += 1
      for (balloon <- balloonModels) {
        if (balloon.nonEmpty)
          balloon.get.applyForceToCenter(buoyancyForce, true)
      } // Keep balloons flying
      //worldBox.step(Gdx.graphics.getDeltaTime, 6, 2)
      worldBox.step(Gdx.graphics.getDeltaTime, 6, 2)
      renderPhysicsObjects()
      if (showPhysicsDebug)
        debugRenderer.render(worldBox, physicsDebugCam.combined)
      if (paintWheel.nonEmpty) {
        val rotateAdjust: Float = (renderTickCounter % 250) * ((2f * Pi) / 250).toFloat
        paintWheel.get.updatePhysics(scene,100, compartmentVolumes.get, compartmentFilledRatios.get, compartmentColors.get, rotateAdjust)
      }
    }
  }

  override def keyDown(keycode: Int): Boolean = {
    var res = false
    Gdx.app.log("ShapeFactorsScreen", s"keyDown(), keyCode = $keycode , scene = $scene")
    if (scene != null) {
      //&& (keycode == Keys.ESCAPE || keycode == Keys.SPACE || keycode == Keys.TAB)
      if (keycode == Keys.ESCAPE) {
        exitScreen()
        res = true
      }

      if (keycode == Keys.F2) {
        showPhysicsDebug = !showPhysicsDebug
        res = true
      }
      if (keycode == Keys.F5) {
        resetPixelDust()
        res = true
      }
    }

    res
  }

  override def update(dt: Float): Unit = {
    super.update(dt)

    if (mainStage != null) {
      //val actorsList = mainStage.getActors
      tickCounter += 1
      if (tickCounter == 100) {
        //animateAnglerFish()
      }
      if (tickCounter >= 50) {

      }
    }
  }

}