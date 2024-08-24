package com.example.demo.post.application.impl

import com.example.demo.post.application.PostService
import com.example.demo.post.entity.Post
import com.example.demo.post.exception.PostNotFoundException
import com.example.demo.post.repository.PostRepository
import org.springframework.stereotype.Service

@Service
class PostServiceImpl(
  private val postRepository: PostRepository
) : PostService {

  override fun validateReturnPost(postId: Long): Post {
    val post: Post = postRepository
      .findOneById(postId) ?: throw PostNotFoundException(postId)

    return post
  }
}
