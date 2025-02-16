package com.example.justdoit.until

import com.example.justdoit.common.BuildInfo
import com.example.justdoit.common.http.BackendApiUrl
import com.example.justdoit.common.http.PathDef
import com.example.justdoit.model.Msg
import com.example.justdoit.until.HttpHelper.ResponseDecoders.*
import tyrian.Cmd
import tyrian.http.*
import zio.Task
import zio.interop.catz.*
import zio.json.*
import com.example.justdoit.util.PrettyLogger
import com.example.justdoit.common.model.*

object HttpHelper:

  // Side Effect -> controlled effect -> produce Message or no Message
  val fetchTodoItems: Cmd[Task, Msg.UpdateTodoList | Msg.Error] =
    val request = Request.get(BackendApiUrl.fetchTodoItems)
    Http.send(request, todoItemsDecoder)

  def createTodo(todo: CreateTodo): Cmd[Task, Msg.NewTodoCreated | Msg.Error] =
    val body    = Body.json(todo.toJson)
    val request = Request.post(BackendApiUrl.createTodo, body)
    Http.send(request, todoItemDecoder)

  def deleteTodo(id: String): Cmd[Task, Msg.TodoDeleted | Msg.Error] =
    val request = Request(Method.Delete, BackendApiUrl.deleteTodo(id))
    Http.send(request, deleteItemDecoder)

  def updateTodo(todo: UpdateTodo): Cmd[Task, Msg.TodoUpdated | Msg.Error] =
    val request = Request(Method.Put, BackendApiUrl.updateTodo, Body.json(todo.toJson))
    Http.send(request, updateTodoResponseDecoder)

  object ResponseDecoders {

    val todoItemsDecoder: Decoder[Msg.UpdateTodoList | Msg.Error] =
      Decoder(
        response =>
          response.body.fromJson[List[TodoItem]] match
            case Right(items) =>
              Msg.UpdateTodoList(items)
            case _ =>
              Msg.UpdateTodoList(Nil)
        ,
        _ => Msg.Error(s"Error when loading data. Is server running on ${BuildInfo.backendBaseUrl} ?")
      )

    val todoItemDecoder: Decoder[Msg.NewTodoCreated | Msg.Error] =
      Decoder(
        response =>
          response.body.fromJson[TodoItem] match
            case Right(item) =>
              Msg.NewTodoCreated(item)
            case _ =>
              Msg.Error(response.body)
        ,
        _ => Msg.Error("Server error")
      )

    val deleteItemDecoder: Decoder[Msg.TodoDeleted | Msg.Error] =
      Decoder(
        response => if response.status.code == 200 then Msg.TodoDeleted(response.body) else Msg.Error("Item not found"),
        _ => Msg.Error("Server Error")
      )

    val updateTodoResponseDecoder: Decoder[Msg.TodoUpdated | Msg.Error] =
      Decoder(
        response =>
          response.body.fromJson[TodoItem] match
            case Right(todo) =>
              Msg.TodoUpdated(todo)
            case _ =>
              Msg.Error("Cannot parse response")
        ,
        _ => Msg.Error("Server error")
      )

  }
