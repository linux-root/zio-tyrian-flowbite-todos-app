package com.example.justdoit

import tyrian.Location
import tyrian.Cmd
import tyrian.http._
import zio.json._
import zio.Task
import zio.json.ast.Json

import com.example.justdoit.page.*
import com.example.justdoit.model.Model.User
import com.example.justdoit.common.util.JwtHelper
import com.example.justdoit.common.model.TodoItem
import com.example.justdoit.model.Model.HomeState
import com.example.justdoit.common.model.TodoItem.Priority
import java.time.LocalDateTime
import com.example.justdoit.model.Model.TodoForm
import com.example.justdoit.util.Datepicker
import scala.scalajs.js.Date
import com.example.justdoit.util.DatetimeHelper
import com.example.justdoit.common.model.CreateTodo

object model {
  enum Msg {
    case NoOp
    case LogMessage(msg: String)
    case NavigateTo(page: Page)
    case DoNavigate(page: Page)
    case UnhandledRoute(path: String)
    case GoToInternet(loc: Location.External)
    case UpdateTodoList(items: List[TodoItem])
    case ToggleDarkMode
    case Error(msg: String)
    case UpdateTodoForm(form: TodoForm)
    case CreateTodo
    case NewTodoCreated(todo: TodoItem)
    case DeleteTodo(id: String)
    case TodoDeleted(id: String)
    case UpdateTodo(todo: common.model.UpdateTodo)
    case TodoUpdated(todo: TodoItem)
    case InitDatepicker(elementId: String)
    case DismissNotification
  }

  /**
   * All frontend states is stored here
   */
  case class Model(currentPage: Page, homeState: HomeState, isDarkMode: Boolean) {
    def toggleDarkMode: Model =
      copy(isDarkMode = !isDarkMode)

    def navigateTo(page: Page): Model = copy(currentPage = page)

  }

  object Model {
    case class User(username: String, accessToken: String, refreshToken: String)

    enum Notification(text: String):
      case Warning(text: String) extends Notification(text)
      case Info(text: String)    extends Notification(text)

    /**
     * Store Home page state
     */
    case class HomeState(todoItems: List[TodoItem], todoForm: TodoForm, notification: Option[Notification])

    case class TodoForm(text: String, priority: Priority, time: String, datepicker: Option[Datepicker]) {
      private def addTimeToJsDate(date: Date, time: String): Date = {
        val parts = time.split(":").map(_.toInt)
        if (parts.length != 2) throw new IllegalArgumentException("Invalid time format, expected HH:mm")

        val newDate = new Date(date.getTime()) // Clone to avoid modifying the original date
        newDate.setHours(parts(0), parts(1), 0, 0) // Set hours and minutes, reset seconds and milliseconds
        newDate
      }
      def getDate: String =
        datepicker
          .map(_.getDate())
          .map(addTimeToJsDate(_, time))
          .map(_.toDateString)
          .getOrElse("datepicker is empty")

      def dueTime: LocalDateTime =
        datepicker
          .map(_.getDate())
          .map(addTimeToJsDate(_, time))
          .map(DatetimeHelper.jsDateToLocalDateTime)
          .get

      def toPayload: CreateTodo =
        CreateTodo(text, priority, dueTime)
    }

    val defaultTime    = "23:00"
    val emptyTodoForm  = TodoForm("", Priority(isUrgent = false, isImportant = false), defaultTime, None)
    val emptyHomeState = HomeState(Nil, emptyTodoForm, None)
    val init: Model    = Model(Page.Home, emptyHomeState, isDarkMode = false)
  }
}
