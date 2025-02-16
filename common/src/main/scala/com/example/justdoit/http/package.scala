package com.example.justdoit.common

import zio.http.Path
import zio.http.Root
import zio.http.Scheme
import zio.http.URL
import zio.http.codec.PathCodec
import zio.http.codec.PathCodec.*

package object http {

  object PathDef {

    /**
     * * Used in Backend project
     */
    val prefix: PathCodec[Unit]                 = Root / "api" / "v1"
    val fetchTodoItems: PathCodec[Unit]         = prefix / "fetch-todo-items"
    val createTodo: PathCodec[Unit]             = prefix / "create-todo"
    val deleteTodo: PathCodec[String]           = prefix / "delete-todo" / string("id")
    val updateTodo: PathCodec[Unit]             = prefix / "update-todo"
    val ping: PathCodec[Unit]                   = prefix / "scala"
    val subscribeServerMessage: PathCodec[Unit] = prefix / "subscribe"
  }

  /**
   * * Used in Frontend project
   */
  object BackendApiUrl {
    import com.example.justdoit.common.BuildInfo.backendBaseUrl

    private def getBackendWebSocketBaseUrl =
      URL
        .decode(backendBaseUrl)
        .map { url =>
          url.scheme match
            case Some(Scheme.HTTP) =>
              backendBaseUrl.replace("http", "ws")
            case Some(Scheme.HTTPS) =>
              backendBaseUrl.replace("https", "wss")
            case _ =>
              "invalid_backendBaseUrl"
        }
        .getOrElse("")

    private def generateUrl(maybePath: Either[String, zio.http.Path]): String =
      backendBaseUrl + maybePath.toOption.get.toString

    private def generateWsUrl(maybePath: Either[String, zio.http.Path]): String =
      getBackendWebSocketBaseUrl + maybePath.toOption.get.toString

    val fetchTodoItems: String         = generateUrl(PathDef.fetchTodoItems.format(()))
    val createTodo: String             = generateUrl(PathDef.createTodo.format(()))
    def deleteTodo(id: String): String = generateUrl(PathDef.deleteTodo.format(id))
    val updateTodo: String             = generateUrl(PathDef.updateTodo.format(()))
    val subscribeServerMessage: String = generateWsUrl(PathDef.subscribeServerMessage.format(()))
  }
}
