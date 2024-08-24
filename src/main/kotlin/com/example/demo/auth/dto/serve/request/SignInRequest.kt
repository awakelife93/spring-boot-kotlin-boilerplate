package com.example.demo.auth.dto.serve.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignInRequest(
  @field:Schema(description = "User Email", nullable = false, format = "email")
  @field:NotBlank(message = "field email is blank")
  @field:Email(message = "field email is not email format")
  val email: String,

  @field:Schema(description = "User Password", nullable = false)
  @field:NotBlank(message = "field password is blank")
  @field:Size(
    min = 8,
    max = 20,
    message = "field password is min size 8 and max size 20"
  )
  val password: String
)
