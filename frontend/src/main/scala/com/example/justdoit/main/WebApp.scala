package com.example.justdoit.main

import tyrian.Html.*
import tyrian.*
import zio.*
import zio.interop.catz.*
import com.softwaremill.quicklens.*
import scala.scalajs.js
import scala.scalajs.js.annotation.*
import tyrian.CSS.*
import tyrian.Routing
import com.example.justdoit.model.*
import com.example.justdoit.model.Model.User
import com.example.justdoit.util.Flowbite
import com.example.justdoit.view.MainContainer
import com.example.justdoit.route.*
import com.example.justdoit.util.*
import com.example.justdoit.page.*
import com.example.justdoit.util.LocalStorageHelper
import com.example.justdoit.until.HttpHelper
import cats.implicits._
import cats.effect.kernel.Sync
import zio.interop.catz.implicits._
import com.example.justdoit.view.components.AddTodoForm
import tyrian.cmds.Logger
import com.example.justdoit.model.Model.Notification

@JSImport("resources/index.css", JSImport.Default)
@js.native
object IndexCSS extends js.Object

@JSExportTopLevel("TyrianApp")
object WebApp extends TyrianZIOApp[Msg, Model]:

  private val css = IndexCSS // Webpack will use this css when bundling

  def main(args: Array[String]): Unit = launch("app") // mount the app to div with id="app"

  def router: Location => Msg =
    case loc: Location.Internal =>
      loc.pathName match
        case Route.Home(_) =>
          Msg.NavigateTo(Page.Home)

        case path @ _ =>
          Msg.UnhandledRoute(path)

    case loc: Location.External => Msg.GoToInternet(loc)

  def init(flags: Map[String, String]): (Model, Cmd[Task, Msg]) =
    val initState = Model.init
    (initState, Cmd.None)

  def update(model: Model): Msg => (Model, Cmd[Task, Msg]) =
    case Msg.NoOp => (model, Cmd.None)

    case Msg.InitDatepicker(elementId) =>
      val dp = new Datepicker(elementId)
      (model.modify(_.homeState.todoForm.datepicker).setTo(Some(dp)), Cmd.None)

    case Msg.UpdateTodoList(items) =>
      (model.modify(_.homeState.todoItems).setTo(items), Flowbite.initCmd)

    case Msg.LogMessage(msg) =>
      (model, PrettyLogger.info(msg))

    case Msg.Error(msg) =>
      (model.modify(_.homeState.notification).setTo(Some(Notification.Warning(msg))), Cmd.None)

    case Msg.DismissNotification =>
      (model.modify(_.homeState.notification).setTo(None), Cmd.None)

    case Msg.ToggleDarkMode =>
      (model.toggleDarkMode, Cmd.None)

    case Msg.GoToInternet(loc) =>
      (model, Nav.loadUrl(loc.url))

    case Msg.NavigateTo(page) =>
      (Model.init, page.doNavigate(model))

    case Msg.DeleteTodo(id) =>
      (model, HttpHelper.deleteTodo(id))

    case Msg.TodoDeleted(id) =>
      (
        model
          .modify(_.homeState.todoItems)
          .using(_.filterNot(_.id == id))
          .modify(_.homeState.notification)
          .setTo(Some(Notification.Info("Todo deleted"))),
        Cmd.None
      )

    case Msg.DoNavigate(page) =>
      (model.modify(_.currentPage).setTo(page), Nav.pushUrl[Task](page.path) |+| Flowbite.initCmd)

    case Msg.UpdateTodoForm(form) =>
      (model.modify(_.homeState.todoForm).setTo(form), Cmd.None)

    case Msg.CreateTodo =>
      (model, HttpHelper.createTodo(model.homeState.todoForm.toPayload))

    case Msg.NewTodoCreated(item) =>
      (
        model
          .modify(_.homeState.todoItems)
          .using(_ :+ item)
          .modify(_.homeState.notification)
          .setTo(Some(Notification.Info("Todo created"))),
        Flowbite.initCmd // To make sure new delete confirmation dialog is handled by FlowbiteJS
      )

    case Msg.UpdateTodo(todo) =>
      (model, HttpHelper.updateTodo(todo))

    case Msg.TodoUpdated(todo) =>
      (
        model
          .modify(_.homeState.todoItems)
          .using(todos => todos.map(x => if x.id == todo.id then todo else x))
          .modify(_.homeState.notification)
          .setTo(Some(Notification.Info("Todo updated"))),
        Cmd.None
      )

    case Msg.UnhandledRoute(path) =>
      (model, PrettyLogger.error("Unhandled route" + path))

  def view(model: Model): Html[Msg] =
    val pageContent = model.currentPage.render(model)
    MainContainer(pageContent, model)

  def subscriptions(model: Model): Sub[Task, Msg] = Sub.None
