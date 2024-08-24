package com.example.demo.auth.dto.serve.response

import com.example.demo.user.constant.UserRole
import com.example.demo.user.entity.User
import io.swagger.v3.oas.annotations.media.Schema

class SignInResponse(
  @field:Schema(
    description = "User Access Token",
    nullable = false
  )
  val accessToken: String,

  @field:Schema(description = "User Id", nullable = false)
  val userId: Long,

  @field:Schema(description = "User Role", nullable = false, implementation = UserRole::class)
  val role: UserRole,

  @field:Schema(description = "User Name", nullable = false)
  val name: String,

  @field:Schema(description = "User Email", nullable = false, format = "email")
  val email: String,
) {
  companion object {
    fun of(user: User, accessToken: String): SignInResponse {
      return with(user) {
        SignInResponse(
          accessToken = accessToken,
          userId = id,
          role = role,
          name = name,
          email = email
        )
      }
    }
  }
}
