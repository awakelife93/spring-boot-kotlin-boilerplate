package com.example.demo.common.response

import io.swagger.v3.oas.annotations.media.Schema

class OkResponse {
  @field:Schema(description = "Response Code")
  var code: Int

  @field:Schema(description = "Response Message")
  var message: String = ""

  @field:Schema(description = "Response Data")
  var data: Any? = null

  private constructor(code: Int) {
    this.code = code
  }

  private constructor(code: Int, message: String) {
    this.code = code
    this.message = message
  }

  private constructor(code: Int, message: String, data: Any?) {
    this.code = code
    this.message = message
    this.data = data
  }

  companion object {
    fun of(code: Int): OkResponse {
      return OkResponse(code)
    }

    fun of(code: Int, message: String): OkResponse {
      return OkResponse(code, message)
    }

    fun of(
      code: Int,
      message: String,
      data: Any?
    ): OkResponse {
      return OkResponse(code, message, data)
    }
  }
}
