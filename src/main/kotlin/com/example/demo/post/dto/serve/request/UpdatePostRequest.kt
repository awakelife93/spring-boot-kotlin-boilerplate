package com.example.demo.post.dto.serve.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class UpdatePostRequest(
  @field:Schema(description = "Post Title", nullable = false)
  @field:NotBlank(message = "field title is blank")
  val title: String,

  @field:Schema(description = "Post Sub Title", nullable = false)
  @field:NotBlank(message = "field subTitle is blank")
  val subTitle: String,

  @field:Schema(description = "Post Content", nullable = false)
  @field:NotBlank(message = "field content is blank")
  val content: String
)
