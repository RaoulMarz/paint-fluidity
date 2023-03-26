package com.codingcrafters.gameobjects

import com.badlogic.gdx.audio.Music
import scala.collection.mutable.Map

class MusicPlayList(/*musicPlayer : Music,*/ playPath : String) {
    var playListMap: Map[String, MusicRecord] = Map.empty[String, MusicRecord]
    var musicPlayers: Map[String, Music] = Map.empty[String, Music]

    def clear(): Unit = {
        playListMap.clear()
    }

    def addMusicRecord(key : String, record : MusicRecord, replace : Boolean = true): Unit = {
        if ((key != null) && !playListMap.keySet.contains(key) ) {
            playListMap += (key -> record)
        } else if (replace) {
            playListMap -= key
            playListMap += (key -> record)
        }
    }
}