package com.example.demo.post.api

import com.example.demo.post.application.impl.ChangePostServiceImpl
import com.example.demo.post.application.impl.GetPostServiceImpl
import com.example.demo.post.dto.serve.request.CreatePostRequest
import com.example.demo.post.dto.serve.request.GetExcludeUsersPostsRequest
import com.example.demo.post.dto.serve.request.UpdatePostRequest
import com.example.demo.post.dto.serve.response.CreatePostResponse
import com.example.demo.post.dto.serve.response.GetPostResponse
import com.example.demo.post.dto.serve.response.UpdatePostResponse
import com.example.demo.post.entity.Post
import com.example.demo.security.SecurityUserItem
import org.assertj.core.api.Assertions.assertThat
import org.instancio.Instancio
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tag("unit-test")
@DisplayName("Unit - Post Controller Test")
@ExtendWith(
  MockitoExtension::class
)
class PostControllerTests {
  private val post: Post = Instancio.create(Post::class.java)

  private val defaultPageable = Pageable.ofSize(1)

  @InjectMocks
  private lateinit var postController: PostController

  @Mock
  private lateinit var getPostServiceImpl: GetPostServiceImpl

  @Mock
  private lateinit var changePostServiceImpl: ChangePostServiceImpl

  @Test
  @DisplayName("Get post by id")
  fun should_AssertGetPostResponse_when_GivenPostId() {
    Mockito.`when`(getPostServiceImpl.getPostById(any<Long>()))
      .thenReturn(GetPostResponse.of(post))

    val response = postController.getPostById(
      post.id
    )

    assertNotNull(response)
    assertNotNull(response.body)
    assertEquals(HttpStatus.OK, response.statusCode)

    val body = requireNotNull(response.body) {
      "Response body must not be null"
    }

    assertEquals(post.id, body.postId)
    assertEquals(post.title, body.title)
    assertEquals(post.subTitle, body.subTitle)
    assertEquals(post.content, body.content)
    assertEquals(post.user.id, body.writer.userId)
  }

  @Test
  @DisplayName("Get post list")
  fun should_AssertListOfGetPostResponse_when_GivenDefaultPageable() {
    Mockito.`when`(getPostServiceImpl.getPostList(any<Pageable>()))
      .thenReturn(listOf(GetPostResponse.of(post)))

    val response = postController.getPostList(
      defaultPageable
    )

    assertNotNull(response)
    assertNotNull(response.body)
    assertEquals(HttpStatus.OK, response.statusCode)

    val body = requireNotNull(response.body) {
      "Response body must not be null"
    }

    assertThat(body).isNotEmpty()
    assertEquals(post.id, body[0].postId)
    assertEquals(post.title, body[0].title)
    assertEquals(post.subTitle, body[0].subTitle)
    assertEquals(post.content, body[0].content)
    assertEquals(post.user.id, body[0].writer.userId)
  }

  @Test
  @DisplayName("Get exclude users post list")
  fun should_AssertListOfGetPostResponse_when_GivenDefaultPageableAndGetExcludeUsersPostsRequest() {
    val getExcludeUsersPostsRequest = Instancio.create(
      GetExcludeUsersPostsRequest::class.java
    )

    Mockito.`when`(getPostServiceImpl.getExcludeUsersPostList(any<GetExcludeUsersPostsRequest>(), any<Pageable>()))
      .thenReturn(listOf(GetPostResponse.of(post)))

    val response = postController.getExcludeUsersPostList(
      getExcludeUsersPostsRequest,
      defaultPageable
    )

    assertNotNull(response)
    assertNotNull(response.body)
    assertEquals(HttpStatus.OK, response.statusCode)

    val body = requireNotNull(response.body) {
      "Response body must not be null"
    }

    assertThat(body).isNotEmpty()
    assertEquals(post.id, body[0].postId)
    assertEquals(post.title, body[0].title)
    assertEquals(post.subTitle, body[0].subTitle)
    assertEquals(post.content, body[0].content)
    assertEquals(post.user.id, body[0].writer.userId)
  }

  @Test
  @DisplayName("Create post")
  fun should_AssertCreatePostResponse_when_GivenUserIdAndCreatePostRequest() {
    val createPostRequest = Instancio.create(
      CreatePostRequest::class.java
    )
    val securityUserItem = Instancio.create(
      SecurityUserItem::class.java
    )

    Mockito.`when`(changePostServiceImpl.createPost(any<Long>(), any<CreatePostRequest>()))
      .thenReturn(CreatePostResponse.of(post))

    val response = postController.createPost(
      createPostRequest,
      securityUserItem
    )

    assertNotNull(response)
    assertNotNull(response.body)
    assertEquals(HttpStatus.CREATED, response.statusCode)

    val body = requireNotNull(response.body) {
      "Response body must not be null"
    }

    assertEquals(post.id, body.postId)
    assertEquals(post.title, body.title)
    assertEquals(post.subTitle, body.subTitle)
    assertEquals(post.content, body.content)
    assertEquals(post.user.id, body.writer.userId)
  }

  @Test
  @DisplayName("Update post")
  fun should_AssertUpdatePostResponse_when_GivenPostIdAndUpdatePostRequest() {
    val updatePostRequest = Instancio.create(
      UpdatePostRequest::class.java
    )

    Mockito.`when`(changePostServiceImpl.updatePost(any<Long>(), any<UpdatePostRequest>()))
      .thenReturn(UpdatePostResponse.of(post))

    val response = postController.updatePost(
      updatePostRequest,
      post.id
    )

    assertNotNull(response)
    assertNotNull(response.body)
    assertEquals(HttpStatus.OK, response.statusCode)

    val body = requireNotNull(response.body) {
      "Response body must not be null"
    }
    
    assertEquals(post.id, body.postId)
    assertEquals(post.title, body.title)
    assertEquals(post.subTitle, body.subTitle)
    assertEquals(post.content, body.content)
    assertEquals(post.user.id, body.writer.userId)
  }

  @Test
  @DisplayName("Delete post")
  fun should_VerifyCallDeletePostMethod_when_GivenPostId() {
    val response = postController.deletePost(post.id)

    assertNotNull(response)
    assertNull(response.body)
    assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

    Mockito.verify(changePostServiceImpl, Mockito.times(1)).deletePost(any<Long>())
  }
}
