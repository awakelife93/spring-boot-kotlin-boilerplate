package com.example.demo.post.application.impl

import com.example.demo.post.application.GetPostService
import com.example.demo.post.dto.serve.request.GetExcludeUsersPostsRequest
import com.example.demo.post.dto.serve.response.GetPostResponse
import com.example.demo.post.entity.Post
import com.example.demo.post.exception.PostNotFoundException
import com.example.demo.post.repository.PostRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetPostServiceImpl(private val postRepository: PostRepository) : GetPostService {

  override fun getPostById(postId: Long): GetPostResponse {
    val post: Post = postRepository
      .findOneById(postId) ?: throw PostNotFoundException(postId)

    return post.let(GetPostResponse::of)
  }

  override fun getPostList(pageable: Pageable): Page<GetPostResponse> {
    return postRepository
      .findAll(pageable)
      .map(GetPostResponse::of)
  }

  override fun getExcludeUsersPostList(
    getExcludeUsersPostsRequest: GetExcludeUsersPostsRequest,
    pageable: Pageable
  ): Page<GetPostResponse> {
    return postRepository.getExcludeUsersPosts(
      getExcludeUsersPostsRequest,
      pageable
    )
  }
}
