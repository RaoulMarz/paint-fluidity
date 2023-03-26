package com.codingcrafters.gameobjects

object Margin extends Enumeration {
  type Margin = Value
  val TOP, BOTTOM, LEFT, RIGHT = Value
}

object EnumerationSignType extends Enumeration {
  type EnumerationSignType = Value
  val SIGN_TYPE_NONE, SIGN_TYPE_BASIC, SIGN_TYPE_GAMETITLE = Value
}

object EnumerationMenuItem extends Enumeration {
  type EnumerationMenuItem = Value
  val MENU_ITEM_NONE, MENU_ITEM_START, MENU_ITEM_OPTIONS, MENU_ITEM_EXIT = Value
}

object EnumerationDialogType extends Enumeration {
  type EnumerationDialogType = Value
  val DIALOG_TYPE_NONE, DIALOG_TYPE_DEFAULT, DIALOG_TYPE_TITLE, DIALOG_TYPE_INFO = Value
}

object EnumerationMoveDirection extends Enumeration {
  type EnumerationMoveDirection = Value
  val MOVE_DIRECTION_NONE, MOVE_DIRECTION_UP, MOVE_DIRECTION_DOWN, MOVE_DIRECTION_LEFT, MOVE_DIRECTION_RIGHT = Value
}