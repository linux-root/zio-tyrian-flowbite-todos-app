package com.example.justdoit.app

import com.example.justdoit.config.AppConfig
import com.example.justdoit.http.DefaultRoutes.*
import zio.*
import zio.Console.*
import zio.http.*
import zio.logging.backend.SLF4J
import com.example.justdoit.service.Storage

object Main extends ZIOAppDefault {

  override def run =
    Server
      .serve(public)
      .provide(
        Server.defaultWithPort(8080),
        Storage.live
      )

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.removeDefaultLoggers >>> SLF4J.slf4j

}
