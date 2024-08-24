package com.example.demo.post.api

import com.example.demo.common.security.SecurityItem
import com.example.demo.common.security.WithMockCustomUser
import com.example.demo.post.application.impl.ChangePostServiceImpl
import com.example.demo.post.application.impl.GetPostServiceImpl
import com.example.demo.post.dto.serve.request.CreatePostRequest
import com.example.demo.post.dto.serve.request.GetExcludeUsersPostsRequest
import com.example.demo.post.dto.serve.request.UpdatePostRequest
import com.example.demo.post.dto.serve.response.CreatePostResponse
import com.example.demo.post.dto.serve.response.GetPostResponse
import com.example.demo.post.dto.serve.response.UpdatePostResponse
import com.example.demo.post.entity.Post
import com.example.demo.post.exception.PostNotFoundException
import org.instancio.Instancio
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ActiveProfiles("test")
@Tag("integration-test")
@DisplayName("integration - Post Controller Test")
@WebMvcTest(
  PostController::class
)
@ExtendWith(MockitoExtension::class)
class PostIntegrationControllerTests : SecurityItem() {
  private val post: Post = Instancio.create(Post::class.java)

  private val defaultPageable = Pageable.ofSize(1)

  @MockBean
  private lateinit var getPostServiceImpl: GetPostServiceImpl

  @MockBean
  private lateinit var changePostServiceImpl: ChangePostServiceImpl

  @BeforeEach
  fun setUp() {
    mockMvc =
      MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
        .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
        .build()
  }

  @Nested
  @DisplayName("GET /api/v1/posts/{postId} Test")
  inner class GetPostByIdTest {
    @Test
    @DisplayName("GET /api/v1/posts/{postId} Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectOKResponseToGetPostResponse_when_GivenPostIdAndUserIsAuthenticated() {
      Mockito.`when`(getPostServiceImpl.getPostById(any<Long>()))
        .thenReturn(GetPostResponse.of(post))

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .get("/api/v1/posts/{postId}", post.id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(commonMessage))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.postId").value(post.id))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value(post.title))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.subTitle").value(post.subTitle))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.content").value(post.content))
        .andExpect(
          MockMvcResultMatchers.jsonPath("$.data.writer.userId").value(post.user.id)
        )
    }

    @Test
    @DisplayName("Not Found Exception GET /api/v1/posts/{postId} Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToPostNotFoundException_when_GivenPostIdAndUserIsAuthenticated() {
      val postNotFoundException = PostNotFoundException(
        post.id
      )

      Mockito.`when`(getPostServiceImpl.getPostById(any<Long>()))
        .thenThrow(postNotFoundException)

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .get("/api/v1/posts/{postId}", post.id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(
          MockMvcResultMatchers.jsonPath("$.message").value(postNotFoundException.message)
        )
        .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isEmpty())
    }

    @Test
    @DisplayName("Unauthorized Exception GET /api/v1/posts/{postId} Response")
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToUnauthorizedException_when_GivenPostIdAndUserIsNotAuthenticated() {
      mockMvc
        .perform(
          MockMvcRequestBuilders
            .get("/api/v1/posts/{postId}", post.id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }
  }

  @Nested
  @DisplayName("GET /api/v1/posts Test")
  inner class GetPostListTest {
    @Test
    @DisplayName("GET /api/v1/posts Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectOKResponseToPageOfGetPostResponse_when_GivenDefaultPageableAndUserIsAuthenticated() {
      Mockito.`when`(getPostServiceImpl.getPostList(any<Pageable>()))
        .thenReturn(PageImpl(listOf(GetPostResponse.of(post)), defaultPageable, 1))

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .get("/api/v1/posts")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(commonMessage))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].postId").value(post.id))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].title").value(post.title))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].subTitle").value(post.subTitle))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].content").value(post.content))
        .andExpect(
          MockMvcResultMatchers.jsonPath("$.data.content[0].writer.userId").value(post.user.id)
        )
    }

    @Test
    @DisplayName("Empty GET /api/v1/posts Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectOKResponseToPageOfGetPostResponseIsEmpty_when_GivenDefaultPageableAndUserIsAuthenticated() {
      Mockito.`when`(getPostServiceImpl.getPostList(any<Pageable>()))
        .thenReturn(PageImpl(listOf(), defaultPageable, 0))

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .get("/api/v1/posts")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(commonMessage))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.content").isEmpty())
    }

    @Test
    @DisplayName("Unauthorized Exception GET /api/v1/posts Response")
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToUnauthorizedException_when_GivenDefaultPageableAndUserIsNotAuthenticated() {
      mockMvc
        .perform(
          MockMvcRequestBuilders
            .get("/api/v1/posts")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }
  }

  @Nested
  @DisplayName("GET /api/v1/posts/exclude-users Test")
  inner class GetExcludeUsersPostListTest {
    private val getExcludeUsersPostsRequest: GetExcludeUsersPostsRequest = Instancio.create(
      GetExcludeUsersPostsRequest::class.java
    )

    @Test
    @DisplayName("GET /api/v1/posts/exclude-users Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectOKResponseToPageOfGetPostResponse_when_GivenDefaultPageableAndGetExcludeUsersPostsRequestAndUserIsAuthenticated() {
      Mockito.`when`(getPostServiceImpl.getExcludeUsersPostList(any<GetExcludeUsersPostsRequest>(), any<Pageable>()))
        .thenReturn(PageImpl(listOf(GetPostResponse.of(post)), defaultPageable, 1))

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .get("/api/v1/posts/exclude-users")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .param(
              "userIds",
              objectMapper.writeValueAsString(
                getExcludeUsersPostsRequest.userIds[0]
              )
            )
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(commonMessage))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].postId").value(post.id))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].title").value(post.title))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].subTitle").value(post.subTitle))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].content").value(post.content))
        .andExpect(
          MockMvcResultMatchers.jsonPath("$.data.content[0].writer.userId").value(post.user.id)
        )
    }

    @Test
    @DisplayName("Empty GET /api/v1/posts/exclude-users Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectOKResponseToPageOfGetPostResponseIsEmpty_when_GivenDefaultPageableAndGetExcludeUsersPostsRequestAndUserIsAuthenticated() {
      Mockito.`when`(getPostServiceImpl.getExcludeUsersPostList(any<GetExcludeUsersPostsRequest>(), any<Pageable>()))
        .thenReturn(PageImpl(listOf(), defaultPageable, 0))

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .get("/api/v1/posts/exclude-users")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .param(
              "userIds",
              objectMapper.writeValueAsString(
                getExcludeUsersPostsRequest.userIds[0]
              )
            )
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(commonMessage))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.content").isEmpty())
    }

    @Test
    @DisplayName("Unauthorized Exception GET /api/v1/posts/exclude-users Response")
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToUnauthorizedException_when_GivenDefaultPageableAndGetExcludeUsersPostsRequestAndUserIsNotAuthenticated() {
      mockMvc
        .perform(
          MockMvcRequestBuilders
            .get("/api/v1/posts/exclude-users")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }
  }

  @Nested
  @DisplayName("PUT /api/v1/posts Test")
  inner class CreatePostTest {
    private val createPostRequest: CreatePostRequest = Instancio.create(
      CreatePostRequest::class.java
    )

    @Test
    @DisplayName("PUT /api/v1/posts Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectOKResponseToCreatePostResponse_when_GivenUserIdAndCreatePostRequestAndUserIsAuthenticated() {
      Mockito.`when`(changePostServiceImpl.createPost(any<Long>(), any<CreatePostRequest>()))
        .thenReturn(CreatePostResponse.of(post))

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .put("/api/v1/posts")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(createPostRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(commonMessage))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.postId").value(post.id))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value(post.title))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.subTitle").value(post.subTitle))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.content").value(post.content))
        .andExpect(
          MockMvcResultMatchers.jsonPath("$.data.writer.userId").value(post.user.id)
        )
    }

    @Test
    @DisplayName("Field Valid Exception PUT /api/v1/posts Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToValidException_when_GivenUserIdAndWrongCreatePostRequestAndUserIsAuthenticated() {
      val wrongCreatePostRequest = createPostRequest.copy(
        title = "",
        subTitle = ""
      )

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .put("/api/v1/posts")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(wrongCreatePostRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        // title:field title is blank, subTitle:field subTitle is blank,
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").isString())
        .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isNotEmpty())
    }

    @Test
    @DisplayName("Unauthorized Exception PUT /api/v1/posts Response")
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToUnauthorizedException_when_GivenUserIdAndCreatePostRequestAndUserIsNotAuthenticated() {
      mockMvc
        .perform(
          MockMvcRequestBuilders
            .put("/api/v1/posts")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }
  }

  @Nested
  @DisplayName("PATCH /api/v1/posts/{postId} Test")
  inner class UpdatePostTest {
    private val updatePostRequest: UpdatePostRequest = Instancio.create(
      UpdatePostRequest::class.java
    )

    @Test
    @DisplayName("PATCH /api/v1/posts/{postId} Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectOKResponseToUpdatePostResponse_when_GivenPostIdAndUpdatePostRequestAndUserIsAuthenticated() {
      Mockito.`when`(changePostServiceImpl.updatePost(any<Long>(), any<UpdatePostRequest>()))
        .thenReturn(UpdatePostResponse.of(post))

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .patch("/api/v1/posts/{postId}", post.id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(updatePostRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(commonMessage))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.postId").value(post.id))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value(post.title))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.subTitle").value(post.subTitle))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.content").value(post.content))
        .andExpect(
          MockMvcResultMatchers.jsonPath("$.data.writer.userId").value(post.user.id)
        )
    }

    @Test
    @DisplayName("Field Valid Exception PATCH /api/v1/posts/{postId} Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToValidException_when_GivenPostIdAndWrongUpdatePostRequestAndUserIsAuthenticated() {
      val wrongUpdatePostRequest = updatePostRequest.copy(
        title = "",
        subTitle = ""
      )

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .patch("/api/v1/posts/{postId}", post.id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(wrongUpdatePostRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(
          MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value())
        ) // subTitle:field subTitle is blank, title:field title is blank,
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").isString())
        .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isNotEmpty())
    }

    @Test
    @DisplayName("Unauthorized Exception PATCH /api/v1/posts/{postId} Response")
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToUnauthorizedException_when_GivenPostIdAndUpdatePostRequestAndUserIsNotAuthenticated() {
      mockMvc
        .perform(
          MockMvcRequestBuilders
            .patch("/api/v1/posts/{postId}", post.id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(updatePostRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }

    @Test
    @DisplayName("Not Found Exception PATCH /api/v1/posts/{postId} Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToPostNotFoundException_when_GivenPostIdAndUpdatePostRequestAndUserIsAuthenticated() {
      val postNotFoundException = PostNotFoundException(
        post.id
      )

      Mockito.`when`(changePostServiceImpl.updatePost(any<Long>(), any<UpdatePostRequest>()))
        .thenThrow(postNotFoundException)

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .patch("/api/v1/posts/{postId}", post.id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(updatePostRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(
          MockMvcResultMatchers.jsonPath("$.message").value(postNotFoundException.message)
        )
        .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isEmpty())
    }
  }

  @Nested
  @DisplayName("DELETE /api/v1/posts/{postId} Test")
  inner class DeletePostTest {
    @Test
    @DisplayName("DELETE /api/v1/posts/{postId} Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectOKResponse_when_GivenPostIdAndUserIsAuthenticated() {
      mockMvc
        .perform(
          MockMvcRequestBuilders
            .delete("/api/v1/posts/{postId}", post.id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(commonMessage))
    }

    @Test
    @DisplayName("Unauthorized Error DELETE /api/v1/posts/{postId} Response")
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToUnauthorizedException_when_GivenPostIdAndUserIsNotAuthenticated() {
      mockMvc
        .perform(
          MockMvcRequestBuilders
            .delete("/api/v1/posts/{postId}", post.id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }
  }
}
