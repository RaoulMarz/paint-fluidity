package com.codingcrafters.gameobjects

import scala.collection.mutable.Map

class StaticAnimationStore() {
  protected var animationItemsStore: Map[String, BaseActor] = Map.empty[String, BaseActor]

  def addStaticAnimation(key: String, animationObject: BaseActor) = {
    if ((key != null) && (animationItemsStore.keySet.contains(key) == false) ) {
      animationItemsStore += (key -> animationObject)
    }
  }

  def getAnimation(key: String): BaseActor = {
    if ( (key != null) && (animationItemsStore.keySet.contains(key)))
      return animationItemsStore(key)
    else
      return null
  }

  def clear() = {
    if (animationItemsStore != null)
      animationItemsStore.clear()
  }

  def getAllKeys(): scala.Array[String] = {
    if (animationItemsStore.isEmpty)
      return null
    var resArray = new scala.Array[String](animationItemsStore.keys.size)
    var idx = 0
    animationItemsStore.keys.foreach((tagKey) => {
      resArray(idx) = tagKey
      idx += 1
    }
    )
    resArray
  }
}