package com.example.justdoit

import com.example.justdoit.page.*
import scala.util.Try

package object route {
  object Route {
    object Home:
      def unapply(path: String): Option[Unit] = if path == "/" then Some(()) else None

    object Login:
      def unapply(path: String): Option[Unit] = if path.startsWith("login") then Some(()) else None

  }

}
