package com.example.justdoit.util

import tyrian.Cmd
import tyrian.cmds.*
import zio.*
import com.example.justdoit.until.HttpHelper
import com.example.justdoit.model.Msg
object Authentication {

  def authenticate(username: String, password: String): Cmd[Task, Msg] =
    HttpHelper.login(username, password)
}
