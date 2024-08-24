package com.example.demo.post.repository

import com.example.demo.post.dto.serve.request.GetExcludeUsersPostsRequest
import com.example.demo.post.dto.serve.response.GetPostResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CustomPostRepository {
  fun getExcludeUsersPosts(
    getExcludeUsersPostsRequest: GetExcludeUsersPostsRequest,
    pageable: Pageable
  ): Page<GetPostResponse>
}
