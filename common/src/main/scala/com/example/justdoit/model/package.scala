package com.example.justdoit.common

import zio.Schedule
import zio.json.*
import zio.json.ast.Json
import zio.schema.annotation.description

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import scala.util.Random
import scala.util.Try
import com.example.justdoit.common.model.TodoItem.*

/**
 * Shared models between Backend and Frontend
 */
package object model {
  case class TodoItem(id: String, text: String, priority: Priority, state: State, dueTime: LocalDateTime)
  case class CreateTodo(text: String, priority: Priority, dueTime: LocalDateTime) {

    // Shared data validation
    def isValid: Boolean =
      text.trim.nonEmpty && dueTime.isAfter(LocalDateTime.now)
  }

  case class UpdateTodo(id: String, text: String, priority: Priority, state: State, dueTime: LocalDateTime) {
    def isValid: Boolean =
      text.trim.nonEmpty

    def toTodoItem: TodoItem =
      TodoItem(
        id,
        text,
        priority,
        state,
        dueTime
      )
  }

  object TodoItem {

    def apply(id: String, text: String, priority: Priority, dueTime: LocalDateTime): TodoItem =
      TodoItem(id, text, priority, State.Pending, dueTime)

    enum State:
      case Pending, Active, Done
      def next: State = this match
        case Pending => Active
        case Active  => Done
        case Done    => Pending

    case class Priority(isUrgent: Boolean, isImportant: Boolean)
    given JsonCodec[Priority] = DeriveJsonCodec.gen[Priority]
    given JsonCodec[State]    = DeriveJsonCodec.gen[State]
    given JsonCodec[TodoItem] = DeriveJsonCodec.gen[TodoItem]
  }

  object CreateTodo {
    given JsonCodec[CreateTodo] = DeriveJsonCodec.gen[CreateTodo]
  }

  object UpdateTodo {
    given JsonCodec[UpdateTodo] = DeriveJsonCodec.gen[UpdateTodo]
  }
}
