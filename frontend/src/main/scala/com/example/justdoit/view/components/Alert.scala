package com.example.justdoit.view.components

import tyrian.Html
import tyrian.Html.*
import tyrian.Html.{attribute => attr}

import com.example.justdoit.model.Msg
import com.example.justdoit.util.ElementIdGenerator
import com.example.justdoit.view.components.Icons.i

object Alert:

  // place holder alert to create animate effect
  val empty: Html[Msg] =
    div(
      cls := "fixed bottom-10 right-10 z-30 blur-xs transform transition-all duration-300 ease-in-out translate-x-full opacity-0 rounded-lg flex items-center shadow p-4 mb-4 bg-white text-purple border-t-4 border-gray-200 dark:text-gray-900 dark:bg-gray-700 dark:border-gray-400"
    )(
      Icons.circleAlert,
      div(cls := "dark:text-gray-100 text-l font-thin px-8 px-8")("empty content"),
      button(
        onClick(Msg.DismissNotification),
        cls := s"text-gray-600 rounded-full bg-gray-100 hover:bg-gray-200 inline-flex items-center justify-center h-8 w-8 dark:bg-gray-700 dark:text-gray-400 dark:hover:bg-gray-900"
      )(
        Icons.X
      )
    )

  def warning(textContent: String): Html[Msg] =
    div(
      cls := "fixed bottom-10 right-10 z-30 transform transition-all duration-300 ease-in-out translate-x-0 opacity-100 rounded-lg flex items-center shadow p-4 mb-4 bg-white text-purple border-t-4 border-gray-200 dark:text-gray-900 dark:bg-gray-700 dark:border-gray-400"
    )(
      div(cls := "text-yellow-400")(Icons.circleAlert),
      div(cls := "dark:text-gray-100 text-l font-thin px-8")(textContent),
      button(
        onClick(Msg.DismissNotification),
        cls := s"text-gray-600 rounded-full hover:bg-gray-200 inline-flex items-center justify-center h-8 w-8 dark:bg-gray-700 dark:text-gray-400 dark:hover:bg-gray-900"
      )(Icons.X)
    )

  def info(textContent: String): Html[Msg] =
    div(
      cls := "fixed bottom-10 right-10 z-30 transform transition-all duration-300 ease-in-out translate-x-0 opacity-100 rounded-lg flex items-center shadow p-4 mb-4 bg-white text-purple border-t-4 border-gray-200 dark:text-gray-900 dark:bg-gray-700 dark:border-gray-400"
    )(
      div(cls := "text-green-400")(Icons.circleCheck),
      div(cls := "text-l dark:text-gray-100 font-thin px-8 px-8")(textContent),
      button(
        onClick(Msg.DismissNotification),
        cls := s"text-gray-600 rounded-full hover:bg-gray-200 inline-flex items-center justify-center h-8 w-8 dark:bg-gray-700 dark:text-gray-400 dark:hover:bg-gray-900"
      )(Icons.X)
    )
