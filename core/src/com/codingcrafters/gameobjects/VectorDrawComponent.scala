package com.codingcrafters.gameobjects

import java.io.File
import java.io.StringWriter
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.graphics.{Color, GL20}
//import com.badlogic.gdx.graphics.g2d.TextureRegion
//import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.{Polygon, Rectangle}
import com.badlogic.gdx.Gdx
import com.codingcrafters.gameobjects.EnumerationSignType.EnumerationSignType

class VectorDrawComponent(
    override val xP: Float,
    override val yP: Float,
    override val s: Stage
) extends BaseActor(xP, yP, s) {
  private var assetGamePath = "game"
  //loadTexture(assetGamePath + "/sign.png")
  var text: String = " "
  var viewing = false
  BaseActor.setWorldBounds(500, 400)
  private val shapeRenderer = new ShapeRenderer
  //  private var firstDraw = true
  
  /*
  private def loadJsonData(dataFile: String): Option[Boolean] = {
    var res: Option[Boolean] = None
    res
  }
  */

  override def draw(batch: Batch, parentAlpha: Float): Unit = { // apply color tint effect
    super.draw(batch, parentAlpha)
    val rect = new Rectangle(xP + 20, yP + 150, 300, 200)
    val c = getColor
    batch.setColor(c.r, c.g, c.b, c.a)
    // if (firstDraw) {
    //  batch.begin
    //  firstDraw = false
    // }
    batch.end
    Gdx.gl.glEnable(GL20.GL_ARRAY_BUFFER_BINDING)
    Gdx.gl.glEnable(GL20.GL_BLEND)
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    Gdx.gl.glLineWidth(20)

    shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
    // shapeRenderer.setColor(0, 0, 0, 1)
    shapeRenderer.setColor(Color.LIME)
    shapeRenderer.circle(xP + 20, yP + 20, 100)
    shapeRenderer.circle(xP + 55, yP + 50, 18)
    shapeRenderer.circle(xP + 90, yP + 50, 18)
    shapeRenderer.polyline(Array(xP + 30, yP + 65, xP + 70, yP + 85, xP + 110, yP + 65))
    shapeRenderer.end()
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
    shapeRenderer.setColor(Color.CORAL)
    shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height)
    shapeRenderer.end()
    batch.begin
  }

  def setText(
      t: String,
      signTypeValue: EnumerationSignType = EnumerationSignType.SIGN_TYPE_BASIC
  ): Unit = {
    text = t
    //signType = signTypeValue
  }

  def getText: String = text

  def setViewing(v: Boolean): Unit = {
    viewing = v
  }

  def isViewing: Boolean = viewing
}

object VectorDrawComponent {
  // implicit val codec: JsonValueCodec[VectorDrawComponent] = JsonCodecMaker.make
}
