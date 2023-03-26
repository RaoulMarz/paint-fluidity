package com.codingcrafters.gameobjects

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap.Format

import java.io.File
import java.io.StringWriter
import scala.math._
import java.time.{LocalDateTime, LocalTime}
import com.badlogic.gdx.utils.{Array, ScreenUtils}
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.graphics.{Color, GL20, OrthographicCamera, Pixmap, PixmapIO, Texture}
import com.badlogic.gdx.graphics.g2d.{Animation, Batch, SpriteBatch, TextureRegion}
import com.badlogic.gdx.graphics.glutils.{FrameBuffer, ShapeRenderer}
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.scenes.scene2d.actions.Actions

import java.time.format.DateTimeFormatter
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.{Polygon, Rectangle, Vector2}
import com.badlogic.gdx.Gdx

class ColorPaintWheel(
                       override val xP: Float,
                       override val yP: Float,
                       override val s: Stage,
                       val regionWidth: Float,
                       val regionHeight: Float,
                       val wheelRadius : Float
                     ) extends BaseActor(xP, yP, s) {
  //frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, boundWidth.toInt, boundHeight.toInt, false)
  private val boundWidth: Float = Gdx.graphics.getWidth.toFloat
  private val boundHeight: Float = Gdx.graphics.getHeight.toFloat
  private val shapeRenderer = new ShapeRenderer
  private val cam = new OrthographicCamera
  private val batch: SpriteBatch = new SpriteBatch()
  private var frameBuffer : Option[FrameBuffer] = Some(new FrameBuffer(Pixmap.Format.RGBA8888, boundWidth.toInt, boundHeight.toInt, false))
  private var primeDisplayTexture : Option[Texture] = None
  private var myPixMap : Option[Pixmap] = None
  private var bufferTextureRegion : Option[TextureRegion] = None

  def updatePhysics(scene : Scene, totalVolume : Int, compartmentVolumes : scala.Array[Int], compartmentFilledRatios : scala.Array[Float],
                    compartmentColors : scala.Array[Color], rotateAdjust : Float): Unit = {
    val compartmentCoverAngles: scala.Array[Float] = new scala.Array[Float](compartmentVolumes.length);
    for (compartmentIndex <- compartmentVolumes.indices) {
      compartmentCoverAngles.update(compartmentIndex, (compartmentVolumes(compartmentIndex).toFloat / totalVolume.toFloat) * (2 * Pi).toFloat)
    }
    val rotateOffset : Float = if (rotateAdjust <= (2 * Pi).toFloat) {
      rotateAdjust
    } else {
      ( (rotateAdjust / (2 * Pi).toFloat) - (rotateAdjust / (2 * Pi).toFloat).toInt) * (2 * Pi).toFloat
    }
    updateRenderer(Color.WHITE, Color.LIME, wheelRadius, compartmentCoverAngles, compartmentColors, rotateOffset)
    //if (scene != null) {
    //  scene.addSegment(new SceneSegment(this, Actions.rotateBy(rotateAdjust, 0.05f)
    //}
  }

  private def savePixmapToFile(imageFileBase : String, pixmap : Pixmap): Unit = {
    try {
      val currentTime = LocalDateTime.now
      val format = "yyyy-MM-dd'#'HH-mm-ss"
      val myFormatObj = DateTimeFormatter.ofPattern(format)
      var fh: FileHandle = null
      fh = new FileHandle(/*"ColorPaintWheel#"*/imageFileBase + {
        currentTime.format(myFormatObj)
      } + ".png")
      PixmapIO.writePNG(fh, pixmap)
    } catch {
      case e: Exception =>
        Gdx.app.log("savePixmapToFile ", s"exception= ${e.getMessage}")
    }
  }

  private def drawStar(shapeDrawHandler: ShapeRenderer, starRect : Rectangle, drawColor : Color, arms : Int, angleOffset : Float = 0f, fancyFill : Boolean = true): Unit = {
    val centerX: Float = starRect.x + (starRect.width / 2)
    val centerY: Float = starRect.y + (starRect.height / 2)
    val sectorPoly: scala.Array[Float] = new scala.Array[Float](4 * arms)
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
    shapeRenderer.setColor(drawColor)
    //val sectorPoly: scala.Array[Float] = new scala.Array[Float](4 * arms)
    val rOuter : Float = (starRect.width / 2)
    val rInner : Float = rOuter * 0.65f
    var i: Int = 0
    val angle: Float = (Pi / arms).toFloat
    while (i < 2 * arms) {
      val r: Float = if ((i & 1) == 0) {
        rOuter
      }
      else {
        rInner
      }
      val p: Vector2 = new Vector2(centerX + (cos((i * angle) + angleOffset) * r).toFloat, centerY + (sin((i * angle) + angleOffset) * r).toFloat)
      sectorPoly.update(0 + (i * 2), p.x)
      sectorPoly.update(1 + (i * 2), p.y)
      i += 1
    }
    /*
    val angle : Float = (Pi / arms).toFloat + angleOffset
    while (i < 2 * arms) {
      val r: Float = if ((i & 1) == 0) {
        rOuter
      }
      else {
        rInner
      }
      val p: Vector2 = new Vector2(centerX + (cos(i * angle) * r).toFloat, centerY + (sin(i * angle) * r).toFloat)
      sectorPoly.update(0 + (i * 2), p.x)
      sectorPoly.update(1 + (i * 2), p.y)
      i += 1
    }
    */
    shapeRenderer.polygon(sectorPoly)
    shapeRenderer.end()
    if (fancyFill) {
      shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
      shapeRenderer.setColor(Color.FOREST)
      val rOuter: Float = (starRect.width / 2) - 8f
      val rInner: Float = rOuter * 0.65f
      var i: Int = 0
      val angle: Float = (Pi / arms).toFloat
      while (i < 2 * arms) {
        val r: Float = if ((i & 1) == 0) {
          rOuter
        }
        else {
          rInner
        }
        val p: Vector2 = new Vector2(centerX + (cos((i * angle) + angleOffset) * r).toFloat, centerY + (sin((i * angle) + angleOffset) * r).toFloat)
        sectorPoly.update(0 + (i * 2), p.x)
        sectorPoly.update(1 + (i * 2), p.y)
        i += 1
      }
      shapeRenderer.polygon(sectorPoly)
      shapeRenderer.end()
    }
  }

  private def drawSector(shapeDrawHandler: ShapeRenderer, centerX: Float, centerY: Float,
                         radius: Float, startAngle : Float, endAngle: Float, drawColor : Color = Color.RED): Unit = {
    val segmentsInCircle : Int = 128
    val angleSpan : Float = (endAngle - startAngle) * 360f / (Pi.toFloat * 2)
    if (angleSpan < 0.01f)
      return
    val plotSegments: Int = ( (angleSpan / 360f) * segmentsInCircle).toInt
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
    shapeRenderer.setColor(/*Color.RED*/ drawColor)

    //arc (float x, float y, float radius, float start, float degrees, int segments)
    shapeRenderer.arc(centerX, centerY, radius, startAngle * (360 / (Pi.toFloat * 2)), angleSpan, plotSegments)
    shapeRenderer.end()

    /*
    val angleSpan = endAngle - startAngle
    val plotSegments : Int = (angleSpan / (Math.PI * 2.0f) * segmentsInCircle).toInt
    val angleSegment : Float = ((Pi * 2.0f) / segmentsInCircle).toFloat
    val sectorPoly: scala.Array[Float] = new scala.Array[Float](plotSegments * 2 + 2)
    sectorPoly.update(0, centerX)
    sectorPoly.update(1, centerY)
    var plotX : Float = 0f
    var plotY : Float = 0f
    var workingAngle = startAngle
    for (plotI <- 0 until plotSegments) {
      plotX = (cos(workingAngle) * radius + centerX).toFloat
      plotY = (sin(workingAngle) * radius + centerY).toFloat
      sectorPoly.update( (plotI + 1) * 2, plotX)
      sectorPoly.update( (plotI + 1) * 2 + 1, plotY)
      workingAngle += angleSegment
    }
    shapeRenderer.polygon(sectorPoly)
    shapeRenderer.end()
     */
  }

  def updateRenderer(circleColor: Color, outlineColor: Color, radius: Float, compartmentCoverAngles: scala.Array[Float],
                     compartmentColors: scala.Array[Color], rotateOffset : Float): Unit = {
    val xPP: Float = 10.0f
    val yPP: Float = 10.0f
    val rect = new Rectangle(10.0f, 10.0f, regionWidth - 20.0f, regionHeight - 20.0f)
    //val batch: SpriteBatch = new SpriteBatch()
    //val frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, boundWidth.toInt, boundHeight.toInt, false)
    cam.setToOrtho(false, regionWidth, regionHeight)
    batch.setProjectionMatrix(cam.combined)
    frameBuffer.get.begin()
    Gdx.gl.glEnable(GL20.GL_ARRAY_BUFFER_BINDING)
    Gdx.gl.glEnable(GL20.GL_BLEND)
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    batch.begin()
    Gdx.gl.glLineWidth(4)
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
    shapeRenderer.setColor(/*Color.WHITE*/circleColor)
    shapeRenderer.circle(regionWidth / 2f, regionHeight / 2f, radius)
    //shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height)
    shapeRenderer.end()
    var lastAngle : Float = 0f
    for (compartmentIndex <- compartmentCoverAngles.indices) {
      val toAngle : Float = lastAngle + compartmentCoverAngles(compartmentIndex)
      drawSector(shapeRenderer, regionWidth / 2f, regionHeight / 2f, radius, rotateOffset + lastAngle, rotateOffset + toAngle, compartmentColors(compartmentIndex))
      lastAngle = toAngle
    }
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
    shapeRenderer.setColor(/*Color.LIME*/outlineColor)
    shapeRenderer.circle(regionWidth / 2f, regionHeight / 2f, radius)
    shapeRenderer.end()

    Gdx.gl.glLineWidth(8)
    val starRect = new Rectangle((regionWidth / 2f) - 20f, (regionHeight / 2f) - 20f, 40f, 40f)
    drawStar(shapeRenderer, starRect, Color.GOLDENROD, 5, rotateOffset, true)
    shapeRenderer.flush()
    batch.flush() // flush batch
    batch.end();

    val texShapes: Texture = frameBuffer.get.getColorBufferTexture
    texShapes.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
    if (!(texShapes.getTextureData.isPrepared)) {
      texShapes.getTextureData.prepare()
    }
    bufferTextureRegion = Some(new TextureRegion(texShapes, 0, 0, regionWidth.toInt, regionHeight.toInt))
    frameBuffer.get.end()

    var textureRegionsArray = new com.badlogic.gdx.utils.Array[TextureRegion]
    textureRegionsArray.add(bufferTextureRegion.get)
    loadAnimationFromTextureRegions(textureRegionsArray, 0.1f, true)
  }

  def updateRenderer(drawColor: Color, fillColor: Color, radius: Float): Unit = {
    val xPP: Float = 10.0f
    val yPP: Float = 10.0f
    val boundWidth: Float = Gdx.graphics.getWidth.toFloat
    val boundHeight: Float = Gdx.graphics.getHeight.toFloat
    val rect = new Rectangle(10.0f, 10.0f, regionWidth - 20.0f, regionHeight - 20.0f)
    //val batch: SpriteBatch = new SpriteBatch()
    //val frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, boundWidth.toInt, boundHeight.toInt, false)
    cam.setToOrtho(false, regionWidth, regionHeight)
    batch.setProjectionMatrix(cam.combined)
    frameBuffer.get.begin()
    Gdx.gl.glEnable(GL20.GL_ARRAY_BUFFER_BINDING)
    Gdx.gl.glEnable(GL20.GL_BLEND)
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    batch.begin()
    Gdx.gl.glLineWidth(4)
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
    shapeRenderer.setColor(Color.WHITE)
    shapeRenderer.circle(regionWidth / 2f, regionHeight / 2f, radius)
    //shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height)
    shapeRenderer.end()
    drawSector(shapeRenderer, regionWidth / 2f, regionHeight / 2f, radius, 0f,  (2 * Pi).toFloat * 0.125f)
    drawSector(shapeRenderer, regionWidth / 2f, regionHeight / 2f, radius, (2 * Pi).toFloat * 0.125f, Pi.toFloat * 0.35f, Color.GOLDENROD)
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
    shapeRenderer.setColor(Color.LIME)
    shapeRenderer.circle(regionWidth / 2f, regionHeight / 2f, radius)
    //shapeRenderer.setColor(Color.PURPLE)
    //shapeRenderer.polyline(scala.Array(xPP + 20, yPP + 155, xPP + 60, yPP + 190,
    //  xPP + 195, yPP + 165, xPP + 420, yPP + 280))
    shapeRenderer.end()
    Gdx.gl.glLineWidth(8)
    val starRect = new Rectangle((regionWidth / 2f) - 20f, (regionHeight / 2f) - 20f, 40f, 40f)
    drawStar(shapeRenderer, starRect, Color.GOLDENROD, 5, 0f,true)
    shapeRenderer.flush()
    batch.flush() // flush batch
    batch.end();

    val texShapes: Texture = frameBuffer.get.getColorBufferTexture
    texShapes.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
    if (!(texShapes.getTextureData.isPrepared)) {
      texShapes.getTextureData.prepare
    }
    bufferTextureRegion = Some(new TextureRegion(texShapes, 0, 0, regionWidth.toInt, regionHeight.toInt))
    myPixMap = Some(Pixmap.createFromFrameBuffer(0, 0, regionWidth.toInt, regionHeight.toInt))
    savePixmapToFile("ColorPaintWheel-Draw#", myPixMap.get)
    frameBuffer.get.end()

    var textureRegionsArray = new com.badlogic.gdx.utils.Array[TextureRegion]
    textureRegionsArray.add(bufferTextureRegion.get)
    loadAnimationFromTextureRegions(textureRegionsArray, 0.1f, true)
  }

  def updateDemoBufferRenderer(drawColor: Color, fillColor: Color, radius : Float): Unit = {
    val xPP: Float = 10.0f
    val yPP: Float = 10.0f
    val boundWidth : Float = Gdx.graphics.getWidth.toFloat
    val boundHeight : Float = Gdx.graphics.getHeight.toFloat
    val rect = new Rectangle(20.0f, 20.0f, regionWidth - 30.0f, regionHeight - 40.0f)
    val batch: SpriteBatch = new SpriteBatch()
    val frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, boundWidth.toInt, boundHeight.toInt, false)
    cam.setToOrtho(false, regionWidth, regionHeight)
    batch.setProjectionMatrix(cam.combined)
    frameBuffer.begin()
    Gdx.gl.glEnable(GL20.GL_ARRAY_BUFFER_BINDING)
    Gdx.gl.glEnable(GL20.GL_BLEND)
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    Gdx.gl.glLineWidth(10)

    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    batch.begin() //.draw(red, 256, 0) // Draw another texture, now unto the framebuffer texture
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
    shapeRenderer.setColor(Color.FIREBRICK)
    shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height)
    shapeRenderer.end()
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
    shapeRenderer.setColor(Color.LIME)
    shapeRenderer.circle(xPP + 20.0f + radius, yPP + 20f + radius, radius)
    shapeRenderer.setColor(Color.PURPLE)
    shapeRenderer.circle(xPP + 75f, yPP + 90f, 22f)
    shapeRenderer.circle(xPP + 120f, yPP + 90f, 22f)
    shapeRenderer.setColor(Color.FOREST)
    shapeRenderer.polyline(scala.Array(xPP + 20, yPP + 155, xPP + 60, yPP + 190,
      xPP + 195, yPP + 165, xPP + 420, yPP + 280))
    shapeRenderer.flush()
    shapeRenderer.end()
    batch.flush() // flush batch
    batch.end();

    val texShapes: Texture = frameBuffer.getColorBufferTexture
    texShapes.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
    if (!(texShapes.getTextureData.isPrepared)) {
      texShapes.getTextureData.prepare
    }
    bufferTextureRegion = Some(new TextureRegion(texShapes, 0, 0, regionWidth.toInt, regionHeight.toInt))
    myPixMap = Some(Pixmap.createFromFrameBuffer(0, 0, regionWidth.toInt, regionHeight.toInt))
    savePixmapToFile("ColorPaintWheel-Draw#", myPixMap.get)
    frameBuffer.end()

    //val texturesCollective = scala.Array(texShapes)
    //loadAnimationFromTextures(texturesCollective, 0.1f, true)
    var textureRegionsArray = new com.badlogic.gdx.utils.Array[TextureRegion]
    textureRegionsArray.add(bufferTextureRegion.get)
    loadAnimationFromTextureRegions(textureRegionsArray, 0.1f, true)
  }

  /*
  def updateRenderer(drawColor: Color, fillColor: Color, radius : Float): Unit = {
    val xPP: Int = 10
    val yPP: Int = 10;
    //Gdx.gl.glEnable(GL20.GL_ARRAY_BUFFER_BINDING)
    //Gdx.gl.glEnable(GL20.GL_BLEND)
    //Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    Gdx.gl.glLineWidth(10)
    myPixMap = Some(new Pixmap(regionWidth.toInt, regionHeight.toInt, Format.RGBA8888))
    //myPixMap.get.setColor(Color.WHITE)
    //myPixMap.get.fill()
    myPixMap.get.setColor(fillColor)
    myPixMap.get.fillRectangle(xPP.toInt, yPP.toInt, regionWidth.toInt, regionHeight.toInt)
    myPixMap.get.setColor(Color.ORANGE)
    myPixMap.get.fillCircle(xPP.toInt + radius.toInt, yPP.toInt + radius.toInt, radius.toInt)
    myPixMap.get.setColor(drawColor)
    myPixMap.get.drawRectangle(yPP.toInt, yPP.toInt, regionWidth.toInt, regionHeight.toInt)
    myPixMap.get.setColor(new Color(0.1f, 0.25f, 0.725f, 1.0f))
    for (n <- 0 until 50) {
      myPixMap.get.drawPixel(n + 5, 7)
      myPixMap.get.drawPixel(n + 5, 8)
      myPixMap.get.drawPixel(n + 5, 9)
      myPixMap.get.drawPixel(n + 10, 10)
      myPixMap.get.drawPixel(n + 15, 12)

      myPixMap.get.drawPixel(n + 15, 14)
      myPixMap.get.drawPixel(n + 15, 15)
      myPixMap.get.drawPixel(n + 15, 16)
      myPixMap.get.drawPixel(n + 15, 17)
    }
    myPixMap.get.setColor(Color.FOREST)
    for (n <- 0 until 120) {
      myPixMap.get.drawPixel(n + 24, 22)
      myPixMap.get.drawPixel(n + 25, 23)
      myPixMap.get.drawPixel(n + 26, 24)
      myPixMap.get.drawPixel(n + 27, 25)
      myPixMap.get.drawPixel(n + 28, 26)
    }
    savePixmapToFile("ColorPaintWheel#", myPixMap.get)

    primeDisplayTexture = Some(new Texture(myPixMap.get))

    val texturesCollective = scala.Array(primeDisplayTexture.get)
    loadAnimationFromTextures(texturesCollective, 0.1f, true)
  }
  */

  /*
  override def draw(batch: Batch, parentAlpha: Float): Unit = { // apply color tint effect
    super.draw(batch, parentAlpha)
    val xPP: Float = 10.0f
    val yPP: Float = 10.0f
    val rect = new Rectangle(xPP + 20, yPP + 20, 300, 200)
    val c = getColor
    batch.setColor(c.r, c.g, c.b, c.a)
    batch.end()


    batch.begin()

    //if (bufferTextureRegion.nonEmpty)
    //  batch.draw(bufferTextureRegion.get, 0, 0)
  }
   */

}
