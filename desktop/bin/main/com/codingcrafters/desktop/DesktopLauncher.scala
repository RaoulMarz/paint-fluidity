package com.codingcrafters.desktop

import com.badlogic.gdx.{Game, Graphics}
import com.codingcrafters.prime.FluidPainterGame
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

import scala.util.control.Breaks.break
//import scala.collection.JavaConversions._

object DesktopLauncher extends App:
    // Create desktop resolution from libgdx ??
    private var mode: Graphics.DisplayMode = null
    private val config: Lwjgl3ApplicationConfiguration = new Lwjgl3ApplicationConfiguration
    config.setResizable(true)
    config.setIdleFPS(30)

    private var maxDisplayWidth : Int = 0
    private var maxDisplayHeight : Int = 0
    for (displayMode: Graphics.DisplayMode <- Lwjgl3ApplicationConfiguration.getDisplayModes) {
      if (displayMode.width > maxDisplayWidth)
        maxDisplayWidth = displayMode.width
    }
    for (displayMode: Graphics.DisplayMode <- Lwjgl3ApplicationConfiguration.getDisplayModes) {
      if ((displayMode.width == maxDisplayWidth) && (displayMode.height > maxDisplayHeight))
        maxDisplayHeight = displayMode.height
    }
    for (displayMode : Graphics.DisplayMode  <- Lwjgl3ApplicationConfiguration.getDisplayModes) {
      if ((displayMode.width == maxDisplayWidth) /*&& (displayMode.height eq maxDisplayHeight)*/) {
        mode = displayMode
        //break
      }
    }
    if (mode != null)
      config.setFullscreenMode(mode)
    else
      config.setWindowedMode(/*1600, 900*/maxDisplayWidth, maxDisplayHeight) //setWindowedMode(1280, 720)
    config.useVsync(true)

    private val myGame = new FluidPainterGame()
    val launcher = new Lwjgl3Application(myGame, config)


