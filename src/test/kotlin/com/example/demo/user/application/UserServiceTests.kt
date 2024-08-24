package com.example.demo.user.application

import com.example.demo.auth.dto.serve.request.SignInRequest
import com.example.demo.user.application.impl.UserServiceImpl
import com.example.demo.user.entity.User
import com.example.demo.user.exception.UserNotFoundException
import com.example.demo.user.exception.UserUnAuthorizedException
import com.example.demo.user.repository.UserRepository
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tag("unit-test")
@DisplayName("Unit - User Service Test")
@ExtendWith(
  MockitoExtension::class
)
class UserServiceTests {
  @InjectMocks
  private lateinit var userServiceImpl: UserServiceImpl

  @Mock
  private lateinit var userRepository: UserRepository

  @Mock
  private lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder

  private val user: User = Instancio.create(User::class.java)

  @Nested
  @DisplayName("Validate And Return User Entity Test")
  inner class ValidateReturnUserTest {
    @Test
    @DisplayName("Success validate and get user entity")
    fun should_AssertUserEntity_when_GivenUserId() {
      Mockito.`when`(userRepository.findOneById(any<Long>())).thenReturn(user)

      val validateUser = userServiceImpl.validateReturnUser(user.id)

      assertNotNull(validateUser)
      assertEquals(user.id, validateUser.id)
      assertEquals(user.email, validateUser.email)
      assertEquals(user.name, validateUser.name)
      assertEquals(user.role, validateUser.role)
    }

    @Test
    @DisplayName("validate and user entity is not found exception")
    fun should_AssertUserNotFoundException_when_GivenUserId() {
      Mockito.`when`(userRepository.findOneById(any<Long>())).thenReturn(null)

      Assertions.assertThrows(
        UserNotFoundException::class.java
      ) { userServiceImpl.validateReturnUser(user.id) }
    }
  }

  @Nested
  @DisplayName("Validate and authenticated Return User Entity")
  inner class ValidateAuthReturnUserTest {
    private val signInRequest: SignInRequest = Instancio.create(SignInRequest::class.java)

    @Test
    @DisplayName("Success validate and authenticated get user entity")
    fun should_AssertUserEntity_when_GivenSignInRequest() {
      Mockito.`when`(userRepository.findOneByEmail(any<String>()))
        .thenReturn(user)

      Mockito.`when`(
        user.validatePassword(
          signInRequest.password,
          bCryptPasswordEncoder
        )
      )
        .thenReturn(true)

      val validateAuthUser = userServiceImpl.validateAuthReturnUser(
        signInRequest
      )

      assertNotNull(validateAuthUser)
      assertEquals(user.id, validateAuthUser.id)
      assertEquals(user.email, validateAuthUser.email)
      assertEquals(user.name, validateAuthUser.name)
      assertEquals(user.role, validateAuthUser.role)
    }

    @Test
    @DisplayName("validate and authenticated user is not found exception")
    fun should_AssertUserNotFoundException_when_GivenSignInRequest() {
      Mockito.`when`(userRepository.findOneByEmail(any<String>()))
        .thenReturn(null)

      Assertions.assertThrows(
        UserNotFoundException::class.java
      ) { userServiceImpl.validateAuthReturnUser(signInRequest) }
    }

    @Test
    @DisplayName("validate and authenticated user is unauthorized exception")
    fun should_AssertUserUnAuthorizedException_when_GivenSignInRequest() {
      Mockito.`when`(userRepository.findOneByEmail(any<String>()))
        .thenReturn(user)

      Mockito.`when`(
        user.validatePassword(
          signInRequest.password,
          bCryptPasswordEncoder
        )
      )
        .thenReturn(false)

      Assertions.assertThrows(
        UserUnAuthorizedException::class.java
      ) { userServiceImpl.validateAuthReturnUser(signInRequest) }
    }
  }
}
