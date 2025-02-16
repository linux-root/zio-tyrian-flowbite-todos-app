package com.example.justdoit.http

import com.example.justdoit.common.http.PathDef
import com.example.justdoit.common.util.JwtHelper.UserClaim
import zio._
import zio.http.*
import zio.http.Charsets
import zio.http.Header.AccessControlAllowOrigin
import zio.http.Header.HeaderType
import zio.http.Header.Origin
import zio.http.Middleware
import zio.http.Middleware.CorsConfig
import zio.http.Middleware.cors
import zio.http.Request
import zio.http.Response
import zio.http.Status
import zio.json.*
import zio.json.JsonDecoder

import scala.reflect.ClassTag
import scala.reflect.classTag
import scala.util.Random
import com.example.justdoit.common.model.TodoItem
import com.example.justdoit.common.model.TodoItem.Priority
import java.time.LocalDateTime
import com.example.justdoit.common.model.TodoItem.State
import com.example.justdoit.service.Storage
import com.example.justdoit.common.model.CreateTodo
import com.example.justdoit.common.http.PathDef.createTodo
import com.example.justdoit.common.model.UpdateTodo
import com.example.justdoit.common.http.PathDef.updateTodo

object DefaultRoutes {

  private def parseBody[T: JsonDecoder: ClassTag](request: Request, validate: T => Boolean = (_: T) => true): IO[Response, T] =
    for {
      bodyStr <- request.body.asString(Charsets.Utf8).orElseFail(Response.text("Not supported request body format").status(Status.BadRequest))
      result <- ZIO
                  .fromEither[String, T](JsonDecoder[T].decodeJson(bodyStr))
                  .orElseFail(Response.text(s"Cannot parse body as ${classTag[T].runtimeClass.getName}").status(Status.BadRequest))
      _ <- ZIO.cond(validate(result), result, Response.text("Data is invalid").status(Status.BadRequest))
    } yield result

  private val corsMiddleWare =
    cors(
      CorsConfig(
        allowedOrigin = { case origin => // TODO: Move to  config
          Some(AccessControlAllowOrigin.Specific(origin))
        }
      )
    )

  val public = Routes(
    Method.GET / Root ->
      handler(
        Response.redirect(URL.decode(PathDef.ping.toString).toOption.get)
      ),
    Method.GET / PathDef.ping ->
      handler(
        Response(
          status = Status.Ok,
          headers = Headers(Header.ContentType(MediaType.text.html)),
          body = Body.fromString(HtmlContent.value)
        )
      ),
    Method.GET / PathDef.fetchTodoItems -> handler(
      for {
        items <- Storage.getAll
      } yield Response.json(items.toJson)
    ),
    Method.POST / PathDef.createTodo -> Handler.fromFunctionZIO[Request](request =>
      for {
        todo        <- parseBody[CreateTodo](request, _.isValid)
        createdTodo <- Storage.create(todo)
      } yield Response.json(createdTodo.toJson)
    ),
    Method.DELETE / PathDef.deleteTodo -> Handler.fromFunctionZIO[(String, Request)]((id, request) =>
      for {
        result <- Storage.delete(id)
      } yield result.map(id => Response.text(id)).getOrElse(Response.status(Status.BadRequest))
    ),
    Method.PUT / PathDef.updateTodo -> Handler.fromFunctionZIO[Request](request =>
      for {
        todo        <- parseBody[UpdateTodo](request, _.isValid)
        updatedTodo <- Storage.update(todo)
      } yield updatedTodo
        .map(x =>
          Response
            .json(x.toJson)
        )
        .getOrElse(
          Response.status(Status.BadRequest)
        )
    )
  ) @@ corsMiddleWare

}
