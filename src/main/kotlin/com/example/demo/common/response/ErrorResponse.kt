package com.example.demo.common.response

import io.swagger.v3.oas.annotations.media.Schema

class ErrorResponse {
  @field:Schema(description = "Error Code")
  var code: Int

  @field:Schema(description = "Error Message")
  var message: String = ""

  @field:Schema(description = "Error Item")
  var errors: Any? = null

  private constructor(code: Int) {
    this.code = code
  }

  private constructor(code: Int, message: String) {
    this.code = code
    this.message = message
  }

  private constructor(code: Int, message: String, errors: Any) {
    this.code = code
    this.message = message
    this.errors = errors
  }

  companion object {
    fun of(code: Int): ErrorResponse {
      return ErrorResponse(code)
    }

    fun of(code: Int, message: String): ErrorResponse {
      return ErrorResponse(code, message)
    }

    fun of(
      code: Int,
      message: String,
      errors: Any
    ): ErrorResponse {
      return ErrorResponse(code, message, errors)
    }
  }
}
