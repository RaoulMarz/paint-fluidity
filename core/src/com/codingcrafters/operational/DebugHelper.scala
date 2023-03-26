package com.codingcrafters.operational

object DebugHelper {

  def printList[A](tagDesc : String, aList : List[A], entrySeparator : String = "\n"): String = {
    var res : String = ""
    if ( (aList != null) && (aList.isEmpty == false) ) {
      for (item <- aList) {
        res += item.toString + entrySeparator
      }
    }
    res
  }
}