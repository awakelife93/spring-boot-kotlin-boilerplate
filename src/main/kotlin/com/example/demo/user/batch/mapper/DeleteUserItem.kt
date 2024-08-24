package com.example.demo.user.batch.mapper

import java.time.LocalDateTime

class DeleteUserItem(
  val id: Long,

  val email: String,

  val name: String,

  val role: String,

  val deletedDt: LocalDateTime
) {
  companion object {
    fun of(
      id: Long,
      email: String,
      name: String,
      role: String,
      deletedDt: LocalDateTime
    ): DeleteUserItem {
      return DeleteUserItem(
        id = id,
        email = email,
        name = name,
        role = role,
        deletedDt = deletedDt
      )
    }
  }
}
