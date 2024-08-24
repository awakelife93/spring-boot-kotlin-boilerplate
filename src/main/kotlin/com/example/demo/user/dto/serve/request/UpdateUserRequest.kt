package com.example.demo.user.dto.serve.request

import com.example.demo.common.annotaction.ValidEnum
import com.example.demo.user.constant.UserRole
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class UpdateUserRequest(
  @field:Schema(description = "User Name", nullable = false)
  @field:NotBlank(message = "field name is blank")
  val name: String,

  @field:Schema(description = "User Role", nullable = false, implementation = UserRole::class)
  @field:ValidEnum(enumClass = UserRole::class, message = "field role is invalid")
  val role: UserRole
)
