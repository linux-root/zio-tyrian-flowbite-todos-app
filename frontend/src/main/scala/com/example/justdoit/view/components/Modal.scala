package com.example.justdoit.view.components

import tyrian.Cmd
import tyrian.Html.{attribute => attr}
import tyrian.Html.*
import tyrian.syntax.*
import tyrian.Html
import tyrian.Elem
import com.example.justdoit.model.Msg
import com.example.justdoit.util.ElementIdGenerator

object Modal:

  def apply(buttonClass: String, buttonChild: Elem[Nothing], headerText: Option[String], modalBody: String => Html[Msg]): Html[Msg] =
    val _id = ElementIdGenerator.generate("modal")
    val modalHeader: Option[Html[Msg]] =
      headerText.map(ht =>
        div(cls := "flex items-center justify-between p-4 md:p-5 border-b rounded-t dark:border-gray-600 border-gray-200")(
          h3(cls := "text-xl font-semibold text-gray-900 dark:text-white")(ht),
          button(
            attr("data-modal-hide", _id),
            cls := "text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white"
          )(Icons.X)
        )
      )

    val modal = div(
      id := _id,
      attr("tabindex", "-1"),
      attr("aria-hidden", "true"),
      cls := "hidden overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 justify-center items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full"
    )(
      div(cls := "relative p-4 max-w-2xl max-h-full")(
        div(cls := "flex flex-col gap-4 bg-white rounded-lg shadow-sm dark:bg-gray-700")(
          modalHeader.orEmpty,
          modalBody(_id)
        )
      )
    )

    div(
      button(attr("data-modal-target", _id), attr("data-modal-toggle", _id), cls := buttonClass)(buttonChild),
      modal
    )
