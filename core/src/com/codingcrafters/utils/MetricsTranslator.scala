package com.codingcrafters.utils

//import com.codingcrafters.SharkWorldLauncher
import com.codingcrafters.constants.Constants

/**
  * Created by alvo on 20.05.17.
  */
object MetricsTranslator {
  def convertMeterToPixel(meter: Float): Float = meter * Constants.PX_M_SCALE
  def convertPixelToMeter(pix: Float): Float = pix / Constants.PX_M_SCALE

  /*
  def widthInMeter: Float = convertPixelToMeter(SharkWorldLauncher.width)
  def heightInMeter: Float = convertPixelToMeter(SharkWorldLauncher.height)

  def halfWidthInMeter: Float = widthInMeter * 0.5f
  def halfHeightInMeter: Float = heightInMeter * 0.5f
   */
}
