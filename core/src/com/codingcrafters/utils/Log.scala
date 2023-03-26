package com.codingcrafters.utils

import com.badlogic.gdx.Gdx

object Log {
  def info[A](tag: String, message: String)(function: => A): A = {
    Gdx.app.log(tag, message)
    function
  }

  def debug[A](tag: String, message: String)(function: => A): A = {
    Gdx.app.debug(tag, message)
    function
  }
}
