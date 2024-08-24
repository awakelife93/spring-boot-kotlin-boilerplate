package com.example.demo.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ConvertUtils {
  fun convertStringToLocalDateTimeFormat(
    time: String,
    pattern: String
  ): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val localDateTime = LocalDateTime.parse(time, formatter)

    return localDateTime
  }

  fun convertLocalDateTimeToStringFormat(
    time: LocalDateTime,
    pattern: String
  ): String = time.format(DateTimeFormatter.ofPattern(pattern))
}
