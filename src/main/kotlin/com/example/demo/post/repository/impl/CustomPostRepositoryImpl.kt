package com.example.demo.post.repository.impl

import com.example.demo.post.dto.serve.request.GetExcludeUsersPostsRequest
import com.example.demo.post.dto.serve.response.GetPostResponse
import com.example.demo.post.entity.QPost.post
import com.example.demo.post.repository.CustomPostRepository
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional

open class CustomPostRepositoryImpl(
  private val jpaQueryFactory: JPAQueryFactory
) : CustomPostRepository {

  @Transactional(readOnly = true)
  override fun getExcludeUsersPosts(
    getExcludeUsersPostsRequest: GetExcludeUsersPostsRequest,
    pageable: Pageable
  ): List<GetPostResponse> {
    return jpaQueryFactory
      .select(
        Projections.fields(
          GetPostResponse::class.java,
          post.id,
          post.title,
          post.content,
          post.user.id
        )
      )
      .from(post)
      .where(post.user.id.notIn(getExcludeUsersPostsRequest.userIds))
      .offset(pageable.offset)
      .limit(pageable.pageSize.toLong())
      .fetch()
  }
}
