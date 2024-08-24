package com.example.demo.post.dto

import com.example.demo.user.entity.User

data class Writer(
  val userId: Long,

  val email: String,

  val name: String
) {
  companion object {
    fun of(user: User): Writer {
      return with(user) {
        Writer(
          userId = id,
          email = email,
          name = name
        )
      }
    }
  }
}
