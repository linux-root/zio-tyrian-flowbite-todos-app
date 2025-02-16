package com.example.justdoit.view.components

import com.example.justdoit.util.ElementIdGenerator
import tyrian.Html
import tyrian.Html.*
import tyrian.Html.{attribute => attr}
import com.example.justdoit.model.Msg
import com.example.justdoit.model.Model.TodoForm
import com.softwaremill.quicklens.*
import com.example.justdoit.common.model.TodoItem.Priority

object AddTodoForm:

  def apply(state: TodoForm, datepickerElementId: String, modalId: String): Html[Msg] =
    val selectPriority = select(
      cls := "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-gray-400 dark:focus:ring-blue-500 dark:focus:border-blue-500",
      onChange {
        case "1" =>
          Msg.UpdateTodoForm(state.copy(priority = Priority(isImportant = false, isUrgent = false)))

        case "2" =>
          Msg.UpdateTodoForm(state.copy(priority = Priority(isImportant = false, isUrgent = true)))

        case "3" =>
          Msg.UpdateTodoForm(state.copy(priority = Priority(isImportant = true, isUrgent = false)))

        case "4" =>
          Msg.UpdateTodoForm(state.copy(priority = Priority(isImportant = true, isUrgent = true)))
      }
    )(
      option(attr("selected", "true"))("Choose Priorty level"),
      option(value := "1")("Not URGENT, not IMPORTANT"),
      option(value := "2")("URGENT, not IMPORTANT"),
      option(value := "3")("Not URGENT, IMPORTANT"),
      option(value := "4")("URGENT and IMPORTANT")
    )

    val datePicker =
      div(cls := "relative mb-4")(
        div(cls := "absolute inset-y-0 start-0 flex items-center ps-3 pointer-events-none")(
          div(cls := "flex items-center w-4 h-4 text-gray-500 dark:text-gray-400")(Icons.calendar)
        ),
        input(
          id          := datepickerElementId,
          value       := state.getDate,
          cls         := "bg-gray-50 border border-gray-400 text-gray-900 dark:text-gray-400 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full ps-10 dark:bg-gray-700 dark:border-gray-600 p-3",
          placeholder := "Due date"
        )
      )

    val timePicker =
      div(cls := "relative")(
        div(cls := "absolute inset-y-0 end-0 top-0 flex items-center pe-3.5 pointer-events-none")(
          div(cls := "flex items-center w-4 h-4 text-gray-500 dark:text-gray-400")(Icons.clock2)
        ),
        input(
          tpe   := "time",
          value := state.time,
          onChange(v => Msg.UpdateTodoForm(state.copy(time = v))),
          cls := "bg-gray-50 border leading-none border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-gray-400 dark:focus:ring-blue-500 dark:focus:border-blue-500"
        )
      )

    div(cls := "flex-col max-w-full mx-auto mb-4 px-16 py-8")(
      input(
        value := state.text,
        onChange(value => Msg.UpdateTodoForm(state.copy(text = value))),
        cls         := "mb-5 shadow-xs bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500 dark:shadow-xs-light",
        placeholder := "What to do?"
      ),
      div(cls := "flex items-start mb-5")(
        selectPriority
      ),
      datePicker,
      timePicker,
      button(
        onClick(Msg.CreateTodo),
        attr("data-modal-hide", modalId),
        cls := "text-white my-4 bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
      )("Create")
    )
