package com.example.demo.post.dto.serve.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreatePostRequest(
  @field:Schema(description = "Post Title", nullable = false)
  @field:NotBlank(message = "field title is blank")
  @field:Size(
    max = 20,
    message = "field title is max size 20"
  )
  val title: String,

  @field:Schema(description = "Post Sub Title", nullable = false)
  @field:NotBlank(message = "field subTitle is blank")
  @field:Size(
    max = 40,
    message = "field subTitle is max size 40"
  )
  val subTitle: String,

  @field:Schema(description = "Post Content", nullable = false)
  @field:NotBlank(message = "field content is blank")
  @field:Size(
    max = 500,
    message = "field content is max size 500"
  )
  val content: String
)
