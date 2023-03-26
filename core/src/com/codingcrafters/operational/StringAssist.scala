package com.codingcrafters.operational

import com.badlogic.gdx.Input.Keys

object StringAssist {

    def KeyToString(keyCode: Int): String = {
        var res : String = ""
        keyCode match {
            case Keys.LEFT => { res = "LEFT" }
            case Keys.RIGHT => { res = "RIGHT"  }
            case Keys.UP => { res = "UP"  }
            case Keys.DOWN => { res = "DOWN"  }
        }
        res
    }
}