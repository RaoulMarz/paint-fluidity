package com.codingcrafters.constants

import com.badlogic.gdx.math.Vector2;

object Constants {

  // Graphics:
  val SCALE = 3.0f
  
  val GRAVITY = new Vector2(0.0f, -9.8f)
  val TIME_STEP: Float = 1.0f / 60.0f
  val VELOCITY_ITERATIONS = 6
  val POSITION_ITERATIONS = 3
  val CHIEF_DAMPING = 5.0f
  val DEBUG_INFO_ENABLED = true
  val BACKGROUND_PATH = "intro/Mister_Shark_Game_Portrait.png"
  val VIEWPORT_SCALE = 1.0f
  val PX_M_SCALE = 200.0f
  val PIXEL_PER_METER = 180f

}
