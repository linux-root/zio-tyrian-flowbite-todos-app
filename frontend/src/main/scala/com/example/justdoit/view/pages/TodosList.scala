package com.example.justdoit.view.pages

import tyrian.Html.*
import tyrian.Html.{attribute => attr}
import tyrian.syntax.*
import tyrian.Html
import com.example.justdoit.model.Msg
import com.example.justdoit.common.model.TodoItem
import com.example.justdoit.common.model.TodoItem.State
import com.example.justdoit.view.components.Icons
import java.time.LocalDateTime
import java.time.Duration
import cats.Monad
import com.example.justdoit.view.components.Modal
import com.example.justdoit.view.components.AddTodoForm
import com.example.justdoit.model.Model.HomeState
import com.example.justdoit.util.DatetimeHelper
import com.example.justdoit.model.Model.Notification
import com.example.justdoit.view.components.Alert
import com.example.justdoit.common.model.UpdateTodo
object TodosList:

  private def deleteConfirmation(id: String, modalId: String): Html[Msg] =
    val hideAttribute = attr("data-modal-hide", modalId)
    div(cls := "bg-white rounded-lg py-4 shadow-sm dark:bg-gray-700")(
      div(cls := "p-4 md:p-5 text-center")(
        div(cls := "mx-auto mb-4 text-gray-400 w-16 h-16 dark:text-gray-200")(Icons.circleAlert),
        h3(cls := "mb-5 text-lg font-normal text-gray-500 dark:text-gray-400")("Are you sure you want to delete this Todo?"),
        div(cls := "mt-8")(
          button(
            onClick(Msg.DeleteTodo(id)),
            hideAttribute,
            cls := "text-white bg-red-600 hover:bg-red-800 focus:ring-4 focus:outline-none focus:ring-red-300 dark:focus:ring-red-800 font-medium rounded-lg text-sm inline-flex items-center px-5 py-2.5 text-center"
          )("Yes"),
          button(
            cls := "py-2.5 px-5 ms-3 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700",
            hideAttribute
          )("No")
        )
      )
    )

  private def item(item: TodoItem): Html[Msg] =
    val (textColor, icon) = item.state match
      case State.Pending =>
        "yellow-400" -> Icons.circleDashed
      case State.Active =>
        "blue-600" -> Icons.circleDotDashed
      case State.Done =>
        "green-400" -> Icons.circleCheck

    li(
      cls := "py-3 sm:py-4"
    )(
      div(
        cls := "flex items-center"
      )(
        button(
          onClick(Msg.UpdateTodo(UpdateTodo(item.id, item.text, item.priority, item.state.next, item.dueTime))),
          cls := s"flex justify-center items-center w-10 h-10 text-$textColor rounded-full hover:text-purple-600"
        )(icon),
        div(
          cls := "flex-1 min-w-0 ms-4"
        )(
          p(
            cls := "text-sm font-medium text-gray-900 truncate dark:text-white"
          )(item.text),
          p(
            cls := "text-sm text-gray-500 truncate dark:text-gray-400"
          )(DatetimeHelper.remainingTime(item.dueTime).getOrElse("You missed it"))
        ),
        Modal("text-gray-500 dark:text-white hover:text-red-400", Icons.trash2, None, modalId => deleteConfirmation(item.id, modalId))
      )
    )

  def apply(homeState: HomeState, datepickerElementId: String): Html[Msg] =
    div(
      cls := "w-full max-w-md p-4 bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700"
    )(
      div(
        cls := "flex items-center justify-between mb-4"
      )(
        h5(
          cls := "text-xl font-bold leading-none text-gray-900 dark:text-white"
        )("Let's do it !"),
        Modal(
          "rounded-full p-2.5 text-white bg-gray-500 hover:bg-gray-600 focus:outline-none focus:ring-purple-300 text-center dark:bg-gray-500 dark:hover:bg-gray-400",
          Icons.plus,
          Some("New TO DO"),
          modalId => AddTodoForm(homeState.todoForm, datepickerElementId, modalId)
        )
      ),
      div(
        cls := "flow-root"
      )(
        ul(
          role := "list",
          cls  := "divide-y divide-gray-200 dark:divide-gray-700"
        )(
          homeState.todoItems.map(item)
        )
      )
    )
