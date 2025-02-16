package com.example.justdoit.service

import com.example.justdoit.common.model.TodoItem
import zio.*
import com.example.justdoit.common.model.CreateTodo
import zio.stm.TRef
import java.util.UUID
import com.example.justdoit.common.model.UpdateTodo

trait Storage {
  def getAll: UIO[List[TodoItem]]
  def create(todo: CreateTodo): UIO[TodoItem]
  def delete(id: String): UIO[Option[String]]
  def update(todo: UpdateTodo): UIO[Option[TodoItem]]
}

final class StorageImpl(store: TRef[List[TodoItem]]) extends Storage {
  override def getAll: UIO[List[TodoItem]] =
    store.get.commit

  override def create(todo: CreateTodo): UIO[TodoItem] =
    for {
      id     <- ZIO.succeed(UUID.randomUUID())
      newItem = TodoItem(id.toString(), todo.text, todo.priority, todo.dueTime)
      _      <- store.update(newItem :: _).commit
    } yield newItem

  override def delete(id: String): UIO[Option[String]] =
    for {
      items <- store.get.commit
      result <- items.find(_.id == id) match
                  case None =>
                    ZIO.succeed(None)
                  case Some(_) =>
                    store.set(items.filterNot(_.id == id)).commit.as(Some(id))
    } yield result

  override def update(todo: UpdateTodo): UIO[Option[TodoItem]] =
    for {
      items <- store.get.commit
      result <- items.find(_.id == todo.id) match
                  case None =>
                    ZIO.succeed(None)
                  case Some(_) =>
                    val updatedItem = todo.toTodoItem
                    store.set(items.map(x => if x.id == todo.id then updatedItem else x)).commit.as(Some(updatedItem))
    } yield result

}

object Storage {

  def getAll: URIO[Storage, List[TodoItem]]                     = ZIO.serviceWithZIO[Storage](_.getAll)
  def create(todo: CreateTodo): URIO[Storage, TodoItem]         = ZIO.serviceWithZIO[Storage](_.create(todo))
  def delete(id: String): URIO[Storage, Option[String]]         = ZIO.serviceWithZIO[Storage](_.delete(id))
  def update(todo: UpdateTodo): URIO[Storage, Option[TodoItem]] = ZIO.serviceWithZIO[Storage](_.update(todo))

  val live: ULayer[Storage] = ZLayer {
    for {
      store <- TRef.make(List.empty[TodoItem]).commit
    } yield new StorageImpl(store)
  }

}
