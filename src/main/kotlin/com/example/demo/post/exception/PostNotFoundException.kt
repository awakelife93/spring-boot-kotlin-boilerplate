package com.example.demo.post.exception

import com.example.demo.common.exception.NotFoundException

class PostNotFoundException(postId: Long) : NotFoundException(
  "Post Not Found postId = $postId"
)
