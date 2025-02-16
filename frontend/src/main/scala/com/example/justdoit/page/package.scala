package com.example.justdoit

import tyrian.Cmd
import tyrian.Html
import tyrian.Html.div
import java.util.UUID
import tyrian.cmds.Logger
import zio.*
import zio.interop.catz.*
import com.example.justdoit.util.PrettyLogger
import com.example.justdoit.until.HttpHelper
import com.example.justdoit.model.*
import com.example.justdoit.view.pages.*
import com.example.justdoit.common.model.TodoItem
import com.example.justdoit.common.model.TodoItem.Priority
import java.time.LocalDateTime
import scala.concurrent.duration.*
import com.example.justdoit.util.ElementIdGenerator

package object page {
  private val datepickerElementId = ElementIdGenerator.generate("dp")

  enum Page(
    val path: String,
    val render: Model => Html[Msg],
    beforeEnter: Model => Cmd[Task, Msg] = _ => Cmd.None, // e.g: side effect for loading data
    val isSecured: Boolean = true
  ):
    def doNavigate(model: Model): Cmd[Task, Msg] = beforeEnter(model) |+| Cmd.emit(Msg.DoNavigate(this))

    case Home
        extends Page(
          "/",
          model => TodosList(model.homeState, datepickerElementId),
          _ => HttpHelper.fetchTodoItems |+| Cmd.emitAfterDelay(Msg.InitDatepicker(datepickerElementId), 100.milliseconds)
        )
}
