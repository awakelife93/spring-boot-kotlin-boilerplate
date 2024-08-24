package com.example.demo.post.application;

import com.example.demo.post.entity.Post;

interface PostService {
  fun validateReturnPost(postId: Long): Post
}
