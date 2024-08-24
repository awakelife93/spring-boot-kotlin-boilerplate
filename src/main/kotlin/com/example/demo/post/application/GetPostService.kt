package com.example.demo.post.application

import com.example.demo.post.dto.serve.request.GetExcludeUsersPostsRequest
import com.example.demo.post.dto.serve.response.GetPostResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetPostService {
  fun getPostById(postId: Long): GetPostResponse

  fun getPostList(pageable: Pageable): Page<GetPostResponse>

  fun getExcludeUsersPostList(
    getExcludeUsersPostsRequest: GetExcludeUsersPostsRequest,
    pageable: Pageable
  ): Page<GetPostResponse>
}
