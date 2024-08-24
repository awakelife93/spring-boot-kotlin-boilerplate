package com.example.demo.user.dto.serve.response

import com.example.demo.user.constant.UserRole
import com.example.demo.user.entity.User
import io.swagger.v3.oas.annotations.media.Schema

data class CreateUserResponse(
  @field:Schema(description = "User Id", nullable = false)
  val userId: Long,

  @field:Schema(description = "User Role", nullable = false, implementation = UserRole::class)
  val role: UserRole,

  @field:Schema(description = "User Name", nullable = false)
  val name: String,

  @field:Schema(description = "User Email", nullable = false, format = "email")
  val email: String,

  @field:Schema(
    description = "User AccessToken",
    nullable = false
  )
  val accessToken: String
) {
  companion object {
    fun of(user: User, accessToken: String): CreateUserResponse {
      return with(user) {
        CreateUserResponse(
          userId = id,
          role = role,
          name = name,
          email = email,
          accessToken = accessToken
        )
      }
    }
  }
}
