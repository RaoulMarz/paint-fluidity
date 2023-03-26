package com.codingcrafters.gameobjects

import java.util

import com.codingcrafters.operational.GeometryUtils
//import scala.collection.JavaConverters
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Group

/** Extends functionality of the LibGDX Actor class. by adding support for
  * textures/animation, collision polygons, movement, world boundaries, and
  * camera scrolling. Most game objects should extend this class; lists of
  * extensions can be retrieved by stage and class name.
  *
  * @see
  *   #Actor
  * @author
  *   Lee Stemkoski
  */
object BaseActor { // stores size of game world for all actors
  private var worldBounds: Rectangle = null

  /** Set world dimensions for use by methods boundToWorld() and scrollTo().
    *
    * @param width
    *   width of world
    * @param height
    *   height of world
    */
  def setWorldBounds(width: Float, height: Float): Unit = {
    worldBounds = new Rectangle(0, 0, width, height)
  }

  /** Set world dimensions for use by methods boundToWorld() and scrollTo().
    *
    * @param BaseActor
    *   whose size determines the world bounds (typically a background image)
    */
  def setWorldBounds(ba: BaseActor): Unit = {
    setWorldBounds(ba.getWidth, ba.getHeight)
  }

  /** Get world dimensions
    *
    * @return
    *   Rectangle whose width/height represent world bounds
    */
  def getWorldBounds: Rectangle = worldBounds

  /** Retrieves a list of all instances of the object from the given stage with
    * the given class name or whose class extends the class with the given name.
    * If no instances exist, returns an empty list. Useful when coding
    * interactions between different types of game objects in update method.
    *
    * @param stage
    *   Stage containing BaseActor instances
    * @param className
    *   name of a class that extends the BaseActor class
    * @return
    *   list of instances of the object in stage which extend with the given
    *   class name
    */
  def getList(stage: Stage, className: String): util.ArrayList[BaseActor] = {
    val list = new util.ArrayList[BaseActor]
    var theClass: Class[_] = null
    try
      theClass = Class.forName(className)
    catch {
      case error: Exception =>
        error.printStackTrace()
    }

    /*
    for (a <- stage.getActors) {
      if (theClass.isInstance(a)) list.add(a.asInstanceOf[BaseActor])
    }
     */
    list
  }

  /** Returns number of instances of a given class (that extends BaseActor).
    *
    * @param className
    *   name of a class that extends the BaseActor class
    * @return
    *   number of instances of the class
    */
  def count(stage: Stage, className: String): Int =
    getList(stage, className).size
}

class BaseActor(val xP: Float, val yP: Float, val s: Stage) extends Group { // call constructor from Actor class
  // perform additional initialization tasks
  setPosition(xP, yP)
  s.addActor(this)
  // initialize animation data
  private var animation: Animation[TextureRegion] = null
  private var elapsedTime: Float = 0.0f
  private var animationPaused = false
  private var velocityVec = new Vector2(0, 0)
  private var accelerationVec = new Vector2(0, 0)
  private var acceleration: Float = 0.0f
  private var maxSpeed: Float = 0.0f
  private var deceleration: Float = 0.0f
  private var adjustScalingTranslation: Boolean = true
  private var frameWidth: Float = 0.0f
  private var frameHeight: Float = 0.0f
  private var boundaryPolygon: Polygon = null

  /** If this object moves completely past the world bounds, adjust its position
    * to the opposite side of the world.
    */
  def wrapAroundWorld(): Unit = {
    if (getX + getWidth < 0) setX(BaseActor.worldBounds.width)
    if (getX > BaseActor.worldBounds.width) setX(-getWidth)
    if (getY + getHeight < 0) setY(BaseActor.worldBounds.height)
    if (getY > BaseActor.worldBounds.height) setY(-getHeight)
  }

  /** Align center of actor at given position coordinates.
    *
    * @param x
    *   x-coordinate to center at
    * @param y
    *   y-coordinate to center at
    */
  def centerAtPosition(x: Float, y: Float): Unit = {
    setPosition(x - getWidth / 2, y - getHeight / 2)
  }

  /** Repositions this BaseActor so its center is aligned with center of other
    * BaseActor. Useful when one BaseActor spawns another.
    *
    * @param other
    *   BaseActor to align this BaseActor with
    */
  def centerAtActor(other: BaseActor): Unit = {
    centerAtPosition(
      other.getX + other.getWidth / 2,
      other.getY + other.getHeight / 2
    )
  }

  /** Sets the animation used when rendering this actor; also sets actor size.
    *
    * @param anim
    *   animation that will be drawn when actor is rendered
    */
  def setAnimation(anim: Animation[TextureRegion]): Unit = {
    animation = anim
    val tr = animation.getKeyFrame(0)
    val w = tr.getRegionWidth
    val h = tr.getRegionHeight
    setSize(w.toFloat, h.toFloat)
    setOrigin(w.toFloat / 2.0f, h.toFloat / 2.0f)
    if (boundaryPolygon == null) setBoundaryRectangle()
  }

  /** Creates an animation from images stored in separate files.
    *
    * @param fileNames
    *   array of names of files containing animation images
    * @param frameDuration
    *   how long each frame should be displayed
    * @param loop
    *   should the animation loop
    * @return
    *   animation created (useful for storing multiple animations)
    */
  def loadAnimationFromFiles(
      fileNames: scala.Array[String],
      frameDuration: Float,
      loop: Boolean
  ): Animation[TextureRegion] = {
    val fileCount = fileNames.length
    val textureArray = new Array[TextureRegion]
    if (fileCount > 0) {
      val file1 = fileNames(0)
      val textureTmp = new Texture(Gdx.files.internal(file1))
      frameWidth = textureTmp.getWidth.toFloat
      frameHeight = textureTmp.getHeight.toFloat
      for (n <- 0 until fileCount) {
        val fileName = fileNames(n)
        val texture = new Texture(Gdx.files.internal(fileName))
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear)
        textureArray.add(new TextureRegion(texture))
      }
    }
    val anim = new Animation[TextureRegion](frameDuration, textureArray)
    if (loop) anim.setPlayMode(Animation.PlayMode.LOOP)
    else anim.setPlayMode(Animation.PlayMode.NORMAL)
    if (animation == null) setAnimation(anim)
    anim
  }

  def loadAnimationFromTextureRegions(
     textureArray: Array[TextureRegion],
     frameDuration: Float,
     loop: Boolean
  ): Animation[TextureRegion] = {
    frameWidth = textureArray.get(0).getRegionWidth
    frameHeight = textureArray.get(0).getRegionHeight
    val anim = new Animation[TextureRegion](frameDuration, textureArray)
    if (loop) anim.setPlayMode(Animation.PlayMode.LOOP)
    else anim.setPlayMode(Animation.PlayMode.NORMAL)
    if (animation == null) setAnimation(anim)
    anim
  }

  def loadAnimationFromTextures(
      texturesList: scala.Array[Texture],
      frameDuration: Float,
      loop: Boolean
  ): Animation[TextureRegion] = {
    val itemsCount = texturesList.length
    val textureArray = new Array[TextureRegion]
    if (itemsCount > 0) {
      val textureTmp = texturesList(0)
      frameWidth = textureTmp.getWidth.toFloat
      frameHeight = textureTmp.getHeight.toFloat
      for (n <- 0 until itemsCount) {
        val texture = texturesList(n)
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear)
        textureArray.add(new TextureRegion(texture))
      }
    }
    val anim = new Animation[TextureRegion](frameDuration, textureArray)
    if (loop) anim.setPlayMode(Animation.PlayMode.LOOP)
    else anim.setPlayMode(Animation.PlayMode.NORMAL)
    if (animation == null) setAnimation(anim)
    anim
  }

  /** Creates an animation from a spritesheet: a rectangular grid of images
    * stored in a single file.
    *
    * @param fileName
    *   name of file containing spritesheet
    * @param rows
    *   number of rows of images in spritesheet
    * @param cols
    *   number of columns of images in spritesheet
    * @param frameDuration
    *   how long each frame should be displayed
    * @param loop
    *   should the animation loop
    * @return
    *   animation created (useful for storing multiple animations)
    */
  def loadAnimationFromSheet(
      fileName: String,
      rows: Int,
      cols: Int,
      frameDuration: Float,
      loop: Boolean
  ): Animation[TextureRegion] = {
    val texture = new Texture(Gdx.files.internal(fileName), true)
    texture.setFilter(TextureFilter.Linear, TextureFilter.Linear)
    frameWidth = (texture.getWidth / cols).toFloat
    frameHeight = (texture.getHeight / rows).toFloat
    val temp = TextureRegion.split(texture, frameWidth.toInt, frameHeight.toInt)
    val textureArray = new Array[TextureRegion]
    for (r <- 0 until rows) {
      for (c <- 0 until cols) {
        textureArray.add(temp(r)(c))
      }
    }
    val anim = new Animation[TextureRegion](frameDuration, textureArray)
    if (loop) anim.setPlayMode(Animation.PlayMode.LOOP)
    else anim.setPlayMode(Animation.PlayMode.NORMAL)
    if (animation == null) setAnimation(anim)
    anim
  }

  def loadAnimationFromSheetScaled(
      fileName: String,
      rows: Int,
      cols: Int,
      frameDuration: Float,
      loop: Boolean,
      scaleX: Float,
      scaleY: Float,
      totalDuration: Float = 0.0f
  ): Animation[TextureRegion] = {
    val texture = new Texture(Gdx.files.internal(fileName), true)
    texture.setFilter(TextureFilter.Linear, TextureFilter.Linear)
    setScaleX(scaleX)
    setScaleY(scaleY)
    frameWidth = (texture.getWidth / cols).toFloat
    frameHeight = (texture.getHeight / rows).toFloat
    val temp = TextureRegion.split(texture, frameWidth.toInt, frameHeight.toInt)
    val textureArray = new Array[TextureRegion]
    for (r <- 0 until rows) {
      for (c <- 0 until cols) {
        textureArray.add(temp(r)(c))
      }
    }
    var doLoop = loop
    val repeatCycles = (totalDuration / frameDuration).toInt
    if (repeatCycles > 1)
      doLoop = true
    val anim = new Animation[TextureRegion](frameDuration, textureArray)
    if (doLoop)
      anim.setPlayMode(Animation.PlayMode.LOOP)
    else
      anim.setPlayMode(Animation.PlayMode.NORMAL)
    if (animation == null) setAnimation(anim)
    anim
  }

  /** Convenience method for creating a 1-frame animation from a single texture.
    *
    * @param fileName
    *   names of image file
    * @return
    *   animation created (useful for storing multiple animations)
    */
  def loadTexture(fileName: String): Animation[TextureRegion] = {
    val fileNames = new scala.Array[String](1)
    fileNames(0) = fileName
    loadAnimationFromFiles(fileNames, 1, true)
  }

  /** Set the pause state of the animation.
    *
    * @param pause
    *   true to pause animation, false to resume animation
    */
  def setAnimationPaused(pause: Boolean): Unit = {
    animationPaused = pause
  }

  /** Checks if animation is complete: if play mode is normal (not looping) and
    * elapsed time is greater than time corresponding to last frame.
    *
    * @return
    */
  def isAnimationFinished: Boolean = animation.isAnimationFinished(elapsedTime)

  /** Sets the opacity of this actor.
    *
    * @param opacity
    *   value from 0 (transparent) to 1 (opaque)
    */
  def setOpacity(opacity: Float): Unit = {
    this.getColor.a = opacity
  }

  /** Set acceleration of this object.
    *
    * @param acc
    *   Acceleration in (pixels/second) per second.
    */
  def setAcceleration(acc: Float): Unit = {
    acceleration = acc
  }

  /** Set deceleration of this object. Deceleration is only applied when object
    * is not accelerating.
    *
    * @param dec
    *   Deceleration in (pixels/second) per second.
    */
  def setDeceleration(dec: Float): Unit = {
    deceleration = dec
  }

  /** Set maximum speed of this object.
    *
    * @param ms
    *   Maximum speed of this object in (pixels/second).
    */
  def setMaxSpeed(ms: Float): Unit = {
    maxSpeed = ms
  }

  /** Set the speed of movement (in pixels/second) in current direction. If
    * current speed is zero (direction is undefined), direction will be set to 0
    * degrees.
    *
    * @param speed
    *   of movement (pixels/second)
    */
  def setSpeed(speed: Float): Unit = { // if length is zero, then assume motion angle is zero degrees
    if (velocityVec.len == 0) velocityVec.set(speed, 0)
    else velocityVec.setLength(speed)
  }

  /** Calculates the speed of movement (in pixels/second).
    *
    * @return
    *   speed of movement (pixels/second)
    */
  def getSpeed: Float = velocityVec.len

  /** Determines if this object is moving (if speed is greater than zero).
    *
    * @return
    *   false when speed is zero, true otherwise
    */
  def isMoving: Boolean = getSpeed > 0

  /** Sets the angle of motion (in degrees). If current speed is zero, this will
    * have no effect.
    *
    * @param angle
    *   of motion (degrees)
    */
  def setMotionAngle(angle: Float): Unit = {
    // velocityVec.setAngle(angle)
    velocityVec.setAngleDeg(angle)
  }

  /** Get the angle of motion (in degrees), calculated from the velocity vector.
    * <br> To align actor image angle with motion angle, use <code>setRotation(
    * getMotionAngle() )</code>.
    *
    * @return
    *   angle of motion (degrees)
    */
  def getMotionAngle: Float = velocityVec.angleDeg() // velocityVec.angle

  /** Update accelerate vector by angle and value stored in acceleration field.
    * Acceleration is applied by <code>applyPhysics</code> method.
    *
    * @param angle
    *   Angle (degrees) in which to accelerate.
    * @see
    *   #acceleration
    * @see
    *   #applyPhysics
    */
  def accelerateAtAngle(angle: Float): Unit = {
    accelerationVec.add(new Vector2(acceleration, 0).setAngleDeg(angle))
  }

  /** Update accelerate vector by current rotation angle and value stored in
    * acceleration field. Acceleration is applied by <code>applyPhysics</code>
    * method.
    *
    * @see
    *   #acceleration
    * @see
    *   #applyPhysics
    */
  def accelerateForward(): Unit = {
    accelerateAtAngle(getRotation)
  }

  /** Adjust velocity vector based on acceleration vector, then adjust position
    * based on velocity vector. <br> If not accelerating, deceleration value is
    * applied. <br> Speed is limited by maxSpeed value. <br> Acceleration vector
    * reset to (0,0) at end of method. <br>
    *
    * @param dt
    *   Time elapsed since previous frame (delta time); typically obtained from
    *   <code>act</code> method.
    * @see
    *   #acceleration
    * @see
    *   #deceleration
    * @see
    *   #maxSpeed
    */
  def applyPhysics(dt: Float): Unit = { // apply acceleration
    velocityVec.add(accelerationVec.x * dt, accelerationVec.y * dt)
    var speed = getSpeed
    // decrease speed (decelerate) when not accelerating
    if (accelerationVec.len == 0) speed -= deceleration * dt
    // keep speed within set bounds
    speed = MathUtils.clamp(speed, 0, maxSpeed)
    // update velocity
    setSpeed(speed)
    // update position according to value stored in velocity vector
    moveBy(velocityVec.x * dt, velocityVec.y * dt)
    // reset acceleration
    accelerationVec.set(0, 0)
  }

  /** Set rectangular-shaped collision polygon. This method is automatically
    * called when animation is set, provided that the current boundary polygon
    * is null.
    *
    * @see
    *   #setAnimation
    */
  def setBoundaryRectangle(): Unit = {
    val w = getWidth
    val h = getHeight
    val vertices = scala.Array(0, 0, w, 0, w, h, 0, h)
    boundaryPolygon = new Polygon(vertices)
  }

  /** Replace default (rectangle) collision polygon with an n-sided polygon.
    * <br> Vertices of polygon lie on the ellipse contained within bounding
    * rectangle. Note: one vertex will be located at point (0,width); a 4-sided
    * polygon will appear in the orientation of a diamond.
    *
    * @param numSides
    *   number of sides of the collision polygon
    */
  def setBoundaryPolygon(numSides: Int): Unit = {
    val w = getWidth
    val h = getHeight
    val vertices = new scala.Array[Float](2 * numSides)
    for (i <- 0 until numSides) {
      val angle = i * 6.28f / numSides
      // x-coordinate
      vertices(2 * i) = w / 2 * MathUtils.cos(angle) + w / 2
      // y-coordinate
      vertices(2 * i + 1) = h / 2 * MathUtils.sin(angle) + h / 2
    }
    boundaryPolygon = new Polygon(vertices)
  }

  /** Returns bounding polygon for this BaseActor, adjusted by Actor's current
    * position and rotation.
    *
    * @return
    *   bounding polygon for this BaseActor
    */
  def getBoundaryPolygon: Polygon = {
    boundaryPolygon.setPosition(getX, getY)
    boundaryPolygon.setOrigin(getOriginX, getOriginY)
    boundaryPolygon.setRotation(getRotation)
    boundaryPolygon.setScale(getScaleX, getScaleY)
    boundaryPolygon
  }

  /** Determine if this BaseActor overlaps other BaseActor (according to
    * collision polygons).
    *
    * @param other
    *   BaseActor to check for overlap
    * @return
    *   true if collision polygons of this and other BaseActor overlap
    * @see
    *   #setBoundaryRectangle
    * @see
    *   #setBoundaryPolygon
    */
  def overlaps(other: BaseActor): Boolean = {
    val poly1 = this.getBoundaryPolygon
    val poly2 = other.getBoundaryPolygon
    // initial test to improve performance
    if (!poly1.getBoundingRectangle.overlaps(poly2.getBoundingRectangle))
      return false
    Intersector.overlapConvexPolygons(poly1, poly2)
  }

  /** Implement a "solid"-like behavior: when there is overlap, move this
    * BaseActor away from other BaseActor along minimum translation vector until
    * there is no overlap.
    *
    * @param other
    *   BaseActor to check for overlap
    * @return
    *   direction vector by which actor was translated, null if no overlap
    */
  def preventOverlap(other: BaseActor): Vector2 = {
    val poly1 = this.getBoundaryPolygon
    val poly2 = other.getBoundaryPolygon
    if (!poly1.getBoundingRectangle.overlaps(poly2.getBoundingRectangle))
      return null
    val mtv = new Intersector.MinimumTranslationVector
    val polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv)
    if (!polygonOverlap) return null
    this.moveBy(mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth)
    mtv.normal
  }

  /** Determine if this BaseActor is near other BaseActor (according to
    * collision polygons).
    *
    * @param distance
    *   amount (pixels) by which to enlarge collision polygon width and height
    * @param other
    *   BaseActor to check if nearby
    * @return
    *   true if collision polygons of this (enlarged) and other BaseActor
    *   overlap
    * @see
    *   #setBoundaryRectangle
    * @see
    *   #setBoundaryPolygon
    */
  def isWithinDistance(distance: Float, other: BaseActor): Boolean = {
    val poly1 = this.getBoundaryPolygon
    val scaleX = (this.getWidth + 2 * distance) / this.getWidth
    val scaleY = (this.getHeight + 2 * distance) / this.getHeight
    poly1.setScale(scaleX, scaleY)
    val poly2 = other.getBoundaryPolygon
    if (!poly1.getBoundingRectangle.overlaps(poly2.getBoundingRectangle))
      return false
    Intersector.overlapConvexPolygons(poly1, poly2)
  }

  /** If an edge of an object moves past the world bounds, adjust its position
    * to keep it completely on screen.
    */
  def boundToWorld(): Unit = {
    if (getX < 0) setX(0)
    if (getX + getWidth > BaseActor.worldBounds.width)
      setX(BaseActor.worldBounds.width - getWidth)
    if (getY < 0) setY(0)
    if (getY + getHeight > BaseActor.worldBounds.height)
      setY(BaseActor.worldBounds.height - getHeight)
  }

  /** Center camera on this object, while keeping camera's range of view
    * (determined by screen size) completely within world bounds.
    */
  def alignCamera(): Unit = {
    val cam = this.getStage.getCamera
    val v = this.getStage.getViewport
    // center camera on actor
    cam.position.set(
      this.getX + this.getOriginX,
      this.getY + this.getOriginY,
      0
    )
    // bound camera to layout
    cam.position.x = MathUtils.clamp(
      cam.position.x,
      cam.viewportWidth / 2,
      BaseActor.worldBounds.width - cam.viewportWidth / 2
    )
    cam.position.y = MathUtils.clamp(
      cam.position.y,
      cam.viewportHeight / 2,
      BaseActor.worldBounds.height - cam.viewportHeight / 2
    )
    cam.update()
  }

  /** Processes all Actions and related code for this object; automatically
    * called by act method in Stage class.
    *
    * @param dt
    *   elapsed time (second) since last frame (supplied by Stage act method)
    */
  override def act(dt: Float): Unit = {
    super.act(dt)
    if (!animationPaused) elapsedTime += dt
  }

  /** Draws current frame of animation; automatically called by draw method in
    * Stage class. <br> If color has been set, image will be tinted by that
    * color. <br> If no animation has been set or object is invisible, nothing
    * will be drawn.
    *
    * @param batch
    *   (supplied by Stage draw method)
    * @param parentAlpha
    *   (supplied by Stage draw method)
    * @see
    *   #setColor
    * @see
    *   #setVisible
    */
  override def draw(batch: Batch, parentAlpha: Float): Unit = { // apply color tint effect
    val c = getColor
    batch.setColor(c.r, c.g, c.b, c.a)
    if (animation != null && isVisible) {
      var offsetX = 0.0f
      var offsetY = 0.0f
      if (
        adjustScalingTranslation && (frameWidth >= 2.0f) && (!GeometryUtils
          .withinTolerance(getScaleX, 1.0f, 0.03f))
      ) {
        offsetX = (1.0f - getScaleX) * frameWidth
        offsetY = (1.0f - getScaleY) * frameHeight
      }
      batch.draw(
        animation.getKeyFrame(elapsedTime),
        getX - offsetX,
        getY - offsetY,
        getOriginX,
        getOriginY,
        getWidth,
        getHeight,
        getScaleX,
        getScaleY,
        getRotation
      )
      // batch.draw
    }
    super.draw(batch, parentAlpha)
  }
}
