package com.example.demo.post.application

import com.example.demo.post.application.impl.PostServiceImpl
import com.example.demo.post.entity.Post
import com.example.demo.post.exception.PostNotFoundException
import com.example.demo.post.repository.PostRepository
import org.instancio.Instancio
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tag("unit-test")
@DisplayName("Unit - Post Service Test")
@ExtendWith(
  MockitoExtension::class
)
class PostServiceTests {
  private val post: Post = Instancio.create(Post::class.java)

  @Mock
  private lateinit var postRepository: PostRepository

  @InjectMocks
  private lateinit var postServiceImpl: PostServiceImpl

  @Nested
  @DisplayName("Validate And Return Post Entity Test")
  inner class ValidateReturnPostTest {
    @Test
    @DisplayName("Success validate and get post entity")
    fun should_AssertPostEntity_when_GivenPostId() {
      Mockito.`when`(postRepository.findOneById(any<Long>())).thenReturn(post)

      val validatePost = postServiceImpl.validateReturnPost(post.id)

      assertNotNull(validatePost)
      assertEquals(post.id, validatePost.id)
      assertEquals(post.title, validatePost.title)
      assertEquals(post.subTitle, validatePost.subTitle)
      assertEquals(post.content, validatePost.content)
      assertEquals(post.user.id, validatePost.user.id)
    }

    @Test
    @DisplayName("validate and post entity is not found exception")
    fun should_AssertPostNotFoundException_when_GivenPostId() {
      Mockito.`when`(postRepository.findOneById(any<Long>())).thenReturn(null)

      Assertions.assertThrows(
        PostNotFoundException::class.java
      ) { postServiceImpl.validateReturnPost(post.id) }
    }
  }
}
