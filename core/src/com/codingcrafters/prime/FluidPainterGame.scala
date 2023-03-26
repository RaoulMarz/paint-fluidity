package com.codingcrafters.prime

import com.codingcrafters.gameobjects.BaseGame
import com.codingcrafters.gameobjects.BaseGame.setActiveScreen
import com.codingcrafters.screens.GameMenuScreen
import com.codingcrafters.screens.ShapeFactorsScreen

class FluidPainterGame extends BaseGame:
  //private def myGameScreen : GameMenuScreen = new GameMenuScreen()
  lazy val myGameScreen : GameMenuScreen = new GameMenuScreen()
  lazy val myShapeFactorsScreen : ShapeFactorsScreen = new ShapeFactorsScreen()

   override def create(): Unit =
    super.create()
    //setActiveScreen(myGameScreen)
    setActiveScreen(myShapeFactorsScreen)
