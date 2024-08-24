package com.example.demo.post.repository

import com.example.demo.post.entity.Post
import com.example.demo.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long>, CustomPostRepository {
  fun findOneById(postId: Long): Post?

  fun findOneByUser(user: User): Post?
}
