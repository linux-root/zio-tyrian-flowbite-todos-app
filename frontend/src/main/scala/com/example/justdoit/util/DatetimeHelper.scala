package com.example.justdoit.util

import scala.scalajs.js.Date
import java.time.LocalDateTime
import java.time.Instant
import java.time.ZoneId
import java.time.Duration

object DatetimeHelper:
  def jsDateToLocalDateTime(jsDate: Date): LocalDateTime = {
    val instant = Instant.ofEpochMilli(jsDate.getTime().toLong) // Convert js.Date to Instant
    LocalDateTime.ofInstant(instant, ZoneId.systemDefault()) // Convert to LocalDateTime
  }

  def remainingTime(dueTime: LocalDateTime): Option[String] = {
    val now = LocalDateTime.now()
    if (dueTime.isBefore(now)) {
      None
    } else {
      val duration = Duration.between(now, dueTime)
      val hours    = duration.toHours
      val minutes  = duration.toMinutes % 60
      Some(s"$hours hours $minutes minutes")
    }
  }
