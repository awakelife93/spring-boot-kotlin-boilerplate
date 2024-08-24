package com.example.demo.auth.application

import com.example.demo.auth.dto.serve.request.SignInRequest
import com.example.demo.security.SecurityUserItem
import com.example.demo.security.component.provider.TokenProvider
import com.example.demo.security.exception.RefreshTokenNotFoundException
import com.example.demo.user.application.impl.UserServiceImpl
import com.example.demo.user.entity.User
import com.example.demo.user.exception.UserNotFoundException
import com.example.demo.user.exception.UserUnAuthorizedException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
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
@DisplayName("Unit - Auth Service Test")
@ExtendWith(
  MockitoExtension::class
)
class AuthServiceTests {
  private val user: User = Instancio.create(User::class.java)
  
  private val defaultAccessToken =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c\n" +  //
      ""

  @Mock
  private lateinit var tokenProvider: TokenProvider

  @Mock
  private lateinit var userServiceImpl: UserServiceImpl

  @InjectMocks
  private lateinit var authService: AuthService

  @Nested
  @DisplayName("Sign In Test")
  inner class SignInTest {
    private val signInRequest: SignInRequest = Instancio.create(SignInRequest::class.java)

    @Test
    @DisplayName("Success sign in")
    fun should_AssertSignInResponse_when_GivenSignInRequest() {
      Mockito.`when`(userServiceImpl.validateAuthReturnUser(any<SignInRequest>()))
        .thenReturn(user)

      Mockito.`when`(tokenProvider.createFullTokens(any<User>()))
        .thenReturn(defaultAccessToken)

      val signInResponse = authService.signIn(signInRequest)

      assertNotNull(signInResponse)
      assertEquals(user.email, signInResponse.email)
      assertEquals(user.name, signInResponse.name)
      assertEquals(defaultAccessToken, signInResponse.accessToken)
    }

    @Test
    @DisplayName("User not found")
    fun should_AssertUserNotFoundException_when_GivenSignInRequest() {
      Mockito.`when`(userServiceImpl.validateAuthReturnUser(any<SignInRequest>()))
        .thenThrow(UserNotFoundException(user.id))

      Assertions.assertThrows(
        UserNotFoundException::class.java
      ) { authService.signIn(signInRequest) }
    }

    @Test
    @DisplayName("User unauthorized")
    fun should_AssertUserUnAuthorizedException_when_GivenSignInRequest() {
      Mockito.`when`(userServiceImpl.validateAuthReturnUser(any<SignInRequest>()))
        .thenThrow(UserUnAuthorizedException(user.email))

      Assertions.assertThrows(
        UserUnAuthorizedException::class.java
      ) { authService.signIn(signInRequest) }
    }
  }

  @Nested
  @DisplayName("Sign Out Test")
  inner class SignOutTest {
    @Test
    @DisplayName("Success sign out")
    fun should_VerifyCallDeleteRefreshToken_when_GivenUserId() {
      authService.signOut(user.id)

      Mockito.verify(tokenProvider, Mockito.times(1)).deleteRefreshToken(any<Long>())
    }
  }

  @Nested
  @DisplayName("Refresh Access Token Test")
  inner class RefreshTokenTest {
    private val securityUserItem: SecurityUserItem = Instancio.create(
      SecurityUserItem::class.java
    )

    @Test
    @DisplayName("Success refresh access token")
    fun should_AssertRefreshAccessTokenResponse_when_GivenSecurityUserItem() {
      Mockito.`when`(tokenProvider.refreshAccessToken(any<SecurityUserItem>()))
        .thenReturn(defaultAccessToken)

      val refreshAccessTokenResponse = authService.refreshAccessToken(
        securityUserItem
      )

      assertNotNull(refreshAccessTokenResponse)
      assertEquals(
        defaultAccessToken,
        refreshAccessTokenResponse.accessToken
      )
    }

    @Test
    @DisplayName("Refresh token is expired")
    fun should_AssertExpiredJwtException_when_GivenSecurityUserItem() {
      val claims = Instancio.create(Claims::class.java)

      Mockito.`when`(
        tokenProvider.refreshAccessToken(any<SecurityUserItem>())
      )
        .thenThrow(
          ExpiredJwtException(
            null,
            claims,
            "JWT expired at ?. Current time: ?, a difference of ? milliseconds.  Allowed clock skew: ? milliseconds."
          )
        )

      Assertions.assertThrows(
        ExpiredJwtException::class.java
      ) { authService.refreshAccessToken(securityUserItem) }
    }

    @Test
    @DisplayName("Refresh token is not found")
    fun should_AssertRefreshTokenNotFoundException_when_GivenSecurityUserItem() {
      Mockito.`when`(tokenProvider.refreshAccessToken(any<SecurityUserItem>()))
        .thenThrow(RefreshTokenNotFoundException(user.id))

      Assertions.assertThrows(
        RefreshTokenNotFoundException::class.java
      ) { authService.refreshAccessToken(securityUserItem) }
    }
  }
}
