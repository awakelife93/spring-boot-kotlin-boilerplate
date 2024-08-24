package com.example.demo.post.repository

import com.example.demo.common.config.JpaAuditConfig
import com.example.demo.common.config.QueryDslConfig
import com.example.demo.common.security.WithMockCustomUser
import com.example.demo.post.dto.serve.request.UpdatePostRequest
import com.example.demo.post.entity.Post
import com.example.demo.user.constant.UserRole
import com.example.demo.user.entity.User
import org.instancio.Instancio
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tag("unit-test")
@DisplayName("Unit - Post Repository Test")
@Import(value = [QueryDslConfig::class, JpaAuditConfig::class])
@DataJpaTest
class PostRepositoryTests(
  @Autowired private val postRepository: PostRepository,
) {
  private val defaultPostTitle = "unit test"

  private val defaultPostSubTitle = "Post Repository Test"

  private val defaultPostContent = "default value for post repository testing"

  private val defaultUserEmail = "awakelife93@gmail.com"

  private val defaultUserPassword = "test_password_123!@"

  private val defaultUserName = "Hyunwoo Park"

  private val defaultUserRole = UserRole.USER

  private lateinit var postEntity: Post

  @BeforeEach
  @Throws(Exception::class)
  fun setUp() {
    postEntity =
      Post(
        defaultPostTitle,
        defaultPostSubTitle,
        defaultPostContent,
        User(
          defaultUserEmail,
          defaultUserName,
          defaultUserPassword,
          defaultUserRole
        )
      )
  }

  @Test
  @DisplayName("Create post")
  @WithMockCustomUser
  fun should_AssertCreatedPostEntity_when_GivenPostEntity() {
    val createPost = postRepository.save(postEntity)

    assertEquals(createPost.id, postEntity.id)
    assertEquals(createPost.title, postEntity.title)
    assertEquals(createPost.subTitle, postEntity.subTitle)
    assertEquals(createPost.content, postEntity.content)
    assertEquals(createPost.user.id, postEntity.user.id)
  }

  @Test
  @DisplayName("Update post")
  @WithMockCustomUser
  fun should_AssertUpdatedPostEntity_when_GivenPostIdAndUpdatePostRequest() {
    val updatePostRequest = Instancio.create(
      UpdatePostRequest::class.java
    )

    val beforeUpdatePost = postRepository.save(postEntity)

    postRepository.save(
      beforeUpdatePost.update(
        updatePostRequest.title,
        updatePostRequest.subTitle,
        updatePostRequest.content
      )
    )

    val afterUpdatePost: Post = requireNotNull(
      postRepository
        .findOneById(beforeUpdatePost.id)
    ) {
      "Post must not be null"
    }

    assertEquals(afterUpdatePost.title, updatePostRequest.title)
    assertEquals(
      afterUpdatePost.subTitle,
      updatePostRequest.subTitle
    )
    assertEquals(afterUpdatePost.content, updatePostRequest.content)
  }

  @Test
  @DisplayName("Delete post")
  @WithMockCustomUser
  fun should_AssertDeletedPostEntity_when_GivenPostId() {
    val beforeDeletePost = postRepository.save(postEntity)

    postRepository.deleteById(beforeDeletePost.id)

    val afterDeletePost: Post? = postRepository
      .findOneById(beforeDeletePost.id)

    assertNull(afterDeletePost)
  }

  @Test
  @DisplayName("Find post by id")
  @WithMockCustomUser
  fun should_AssertFindPostEntity_when_GivenPostId() {
    val beforeFindPost = postRepository.save(postEntity)

    val afterFindPost: Post = requireNotNull(
      postRepository
        .findOneById(beforeFindPost.id)
    ) {
      "Post must not be null"
    }

    assertEquals(beforeFindPost.id, afterFindPost.id)
    assertEquals(beforeFindPost.title, afterFindPost.title)
    assertEquals(beforeFindPost.subTitle, afterFindPost.subTitle)
    assertEquals(beforeFindPost.content, afterFindPost.content)
    assertEquals(
      beforeFindPost.user.id,
      afterFindPost.user.id
    )
  }

  @Test
  @DisplayName("Find post by user")
  @WithMockCustomUser
  fun should_AssertFindPostEntity_when_GivenUser() {
    val beforeFindPost = postRepository.save(postEntity)

    val afterFindPost: Post = requireNotNull(
      postRepository
        .findOneByUser(beforeFindPost.user)
    ) {
      "Post must not be null"
    }

    assertEquals(beforeFindPost.id, afterFindPost.id)
    assertEquals(beforeFindPost.title, afterFindPost.title)
    assertEquals(beforeFindPost.subTitle, afterFindPost.subTitle)
    assertEquals(beforeFindPost.content, afterFindPost.content)
    assertEquals(
      beforeFindPost.user.id,
      afterFindPost.user.id
    )
  }
}
