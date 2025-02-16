package com.example.justdoit.util

import scala.scalajs.js
import scala.scalajs.js.annotation._
import org.scalajs.dom.Element
import zio._
import scala.scalajs.js.Date

@js.native
@JSImport("js/datepicker.js")
class Datepicker(id: String) extends js.Object {
  def getDate(): Date = js.native
}
