package com.example.demo.auth.api

import com.example.demo.auth.application.AuthService
import com.example.demo.auth.dto.serve.request.SignInRequest
import com.example.demo.auth.dto.serve.response.RefreshAccessTokenResponse
import com.example.demo.auth.dto.serve.response.SignInResponse
import com.example.demo.security.SecurityUserItem
import com.example.demo.user.entity.User
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
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tag("unit-test")
@DisplayName("Unit - Auth Controller Test")
@ExtendWith(
  MockitoExtension::class
)
class AuthControllerTests {
  @InjectMocks
  private lateinit var authController: AuthController

  @Mock
  private lateinit var authService: AuthService

  private val user: User = Instancio.create(User::class.java)

  private val defaultAccessToken =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c\n" +  //
      ""

  @Test
  @DisplayName("Sign in")
  fun should_AssertSignInResponse_when_GivenSignInRequest() {
    val signInRequest = Instancio.create(SignInRequest::class.java)

    Mockito.`when`(authService.signIn(any<SignInRequest>()))
      .thenReturn(SignInResponse.of(user, defaultAccessToken))

    val response = authController.signIn(
      signInRequest
    )

    assertNotNull(response)
    assertNotNull(response.body)
    assertEquals(HttpStatus.OK, response.statusCode)

    val body = requireNotNull(response.body) {
      "Response body must not be null"
    }

    assertEquals(user.id, body.userId)
    assertEquals(user.email, body.email)
    assertEquals(user.name, body.name)
    assertEquals(user.role, body.role)
    assertEquals(defaultAccessToken, body.accessToken)
  }

  @Test
  @DisplayName("Sign out")
  fun should_AssertSignOutVoidResponse_when_GivenSecurityUserItem() {
    val securityUserItem = Instancio.create(
      SecurityUserItem::class.java
    )

    val response = authController.signOut(securityUserItem)

    assertNotNull(response)
    assertNull(response.body)
    assertEquals(HttpStatus.OK, response.statusCode)
  }

  @Test
  @DisplayName("Refresh access token")
  fun should_AssertRefreshAccessTokenResponse_when_GivenSecurityUserItem() {
    val securityUserItem = Instancio.create(
      SecurityUserItem::class.java
    )

    Mockito.`when`(authService.refreshAccessToken(any<SecurityUserItem>()))
      .thenReturn(RefreshAccessTokenResponse.of(defaultAccessToken))

    val response = authController.refreshAccessToken(
      securityUserItem
    )

    assertNotNull(response)
    assertNotNull(response.body)
    assertEquals(HttpStatus.CREATED, response.statusCode)

    val body = requireNotNull(response.body) {
      "Response body must not be null"
    }
    
    assertEquals(defaultAccessToken, body.accessToken)
  }
}
