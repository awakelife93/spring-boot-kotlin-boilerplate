package com.example.demo.post.dto.serve.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

data class GetExcludeUsersPostsRequest(
  @field:Schema(description = "User Ids", nullable = false)
  @field:Size(
    min = 1,
    message = "field userIds is empty"
  )
  val userIds: MutableList<Long> = mutableListOf()
)
