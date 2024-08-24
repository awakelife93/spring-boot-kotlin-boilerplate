package com.example.demo.post.application

import com.example.demo.post.dto.serve.request.CreatePostRequest
import com.example.demo.post.dto.serve.request.UpdatePostRequest
import com.example.demo.post.dto.serve.response.CreatePostResponse
import com.example.demo.post.dto.serve.response.UpdatePostResponse

interface ChangePostService {
  fun updatePost(
    postId: Long,
    updatePostRequest: UpdatePostRequest
  ): UpdatePostResponse

  fun createPost(
    userId: Long,
    createPostRequest: CreatePostRequest,
  ): CreatePostResponse

  fun deletePost(postId: Long)
}
