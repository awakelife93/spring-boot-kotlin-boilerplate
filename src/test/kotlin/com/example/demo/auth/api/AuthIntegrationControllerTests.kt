package com.example.demo.auth.api

import com.example.demo.auth.application.AuthService
import com.example.demo.auth.dto.serve.request.SignInRequest
import com.example.demo.auth.dto.serve.response.RefreshAccessTokenResponse
import com.example.demo.auth.dto.serve.response.SignInResponse.Companion.of
import com.example.demo.common.security.SecurityItem
import com.example.demo.common.security.WithMockCustomUser
import com.example.demo.security.SecurityUserItem
import com.example.demo.security.exception.RefreshTokenNotFoundException
import com.example.demo.user.entity.User
import com.example.demo.user.exception.UserNotFoundException
import com.example.demo.user.exception.UserUnAuthorizedException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import org.instancio.Instancio
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
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
@DisplayName("integration - Auth Controller Test")
@WebMvcTest(
  AuthController::class
)
@ExtendWith(MockitoExtension::class)
class AuthIntegrationControllerTests : SecurityItem() {
  @MockBean
  private lateinit var authService: AuthService

  private val user: User = Instancio.create(User::class.java)

  private val defaultUserEmail = "awakelife93@gmail.com"

  private val defaultUserPassword = "test_password_123!@"

  private val defaultAccessToken =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c\n" +  //
      ""

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
  @DisplayName("POST /api/v1/auth/signIn Test")
  inner class SignInTest {
    private val mockSignInRequest = Instancio.create(SignInRequest::class.java)
    private val signInRequest: SignInRequest = mockSignInRequest.copy(
      email = defaultUserEmail,
      password = defaultUserPassword
    )

    @Test
    @DisplayName("POST /api/v1/auth/signIn Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectOKResponseToSignInResponse_when_GivenSignInRequest() {
      Mockito.`when`(authService.signIn(any<SignInRequest>()))
        .thenReturn(of(user, defaultAccessToken))

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .post("/api/v1/auth/signIn")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(signInRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(commonStatus))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(commonMessage))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.userId").value(user.id))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value(user.email))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(user.name))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.role").value(user.role.name))
    }

    @Test
    @DisplayName("Field Valid Exception POST /api/v1/auth/signIn Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToValidException_when_GivenWrongSignInRequest() {
      val wrongSignInRequest: SignInRequest = signInRequest.copy(
        email = "wrong_email_format",
        password = "1234"
      )

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .post("/api/v1/auth/signIn")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(wrongSignInRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(
          MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value())
        ) // email:field email is not email format, password:field password is min size 8 and max size 20,
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").isString())
        .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isNotEmpty())
    }

    @Test
    @DisplayName("UnAuthorized Exception POST /api/v1/auth/signIn Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToUserUnAuthorizedException_when_GivenSignInRequest() {
      val userUnAuthorizedException = UserUnAuthorizedException(
        user.id
      )

      Mockito.`when`(authService.signIn(any<SignInRequest>()))
        .thenThrow(userUnAuthorizedException)

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .post("/api/v1/auth/signIn")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(signInRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andExpect(
          MockMvcResultMatchers.jsonPath("$.message").value(userUnAuthorizedException.message)
        )
        .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isEmpty())
    }

    @Test
    @DisplayName("Not Found Exception POST /api/v1/auth/signIn Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToUserNotFoundException_when_GivenSignInRequest() {
      val userNotFoundException = UserNotFoundException(
        user.id
      )

      Mockito.`when`(authService.signIn(any<SignInRequest>()))
        .thenThrow(userNotFoundException)

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .post("/api/v1/auth/signIn")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(signInRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(
          MockMvcResultMatchers.jsonPath("$.message").value(userNotFoundException.message)
        )
        .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isEmpty())
    }
  }

  @Nested
  @DisplayName("POST /api/v1/auth/signOut Test")
  inner class SignOutTest {
    @Test
    @DisplayName("POST /api/v1/auth/signOut Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectOKResponseToSignOutVoidResponse_when_GivenSecurityUserItemAndUserIsAuthenticated() {
      mockMvc
        .perform(
          MockMvcRequestBuilders
            .post("/api/v1/auth/signOut")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(commonStatus))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(commonMessage))
    }

    @Test
    @DisplayName("Unauthorized Exception POST /api/v1/auth/signOut Response")
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToUnauthorizedException_when_GivenSecurityUserItemAndUserIsNotAuthenticated() {
      mockMvc
        .perform(
          MockMvcRequestBuilders
            .post("/api/v1/auth/signOut")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }
  }

  @Nested
  @DisplayName("POST /api/v1/auth/refresh Test")
  inner class RefreshAccessTokenTest {
    @Test
    @DisplayName("POST /api/v1/auth/refresh Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectOKResponseToRefreshAccessTokenResponse_when_GivenSecurityUserItemAndUserIsAuthenticated() {
      Mockito.`when`(authService.refreshAccessToken(any<SecurityUserItem>()))
        .thenReturn(RefreshAccessTokenResponse.of(defaultAccessToken))

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .post("/api/v1/auth/refresh")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(commonMessage))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").value(defaultAccessToken))
    }

    @Test
    @DisplayName("Refresh Token Not Found Unauthorized Exception POST /api/v1/auth/refresh Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToRefreshTokenNotFoundException_when_GivenSecurityUserItemAndUserIsAuthenticated() {
      val refreshTokenNotFoundException = RefreshTokenNotFoundException(
        user.id
      )

      Mockito.`when`(authService.refreshAccessToken(any<SecurityUserItem>()))
        .thenThrow(refreshTokenNotFoundException)

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .post("/api/v1/auth/refresh")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andExpect(
          MockMvcResultMatchers.jsonPath("$.message")
            .value(refreshTokenNotFoundException.message)
        )
        .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isEmpty())
    }

    @Test
    @DisplayName("Refresh Token Expired Exception POST /api/v1/auth/refresh Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToExpiredJwtException_when_GivenSecurityUserItemAndUserIsAuthenticated() {
      val claims = Instancio.create(Claims::class.java)

      val expiredJwtException = ExpiredJwtException(
        null,
        claims,
        "JWT expired at ?. Current time: ?, a difference of ? milliseconds.  Allowed clock skew: ? milliseconds."
      )

      Mockito.`when`(authService.refreshAccessToken(any<SecurityUserItem>()))
        .thenThrow(expiredJwtException)

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .post("/api/v1/auth/refresh")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andExpect(
          MockMvcResultMatchers.jsonPath("$.message").value(expiredJwtException.message)
        )
        .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isEmpty())
    }

    @Test
    @DisplayName("Unauthorized Exception POST /api/v1/auth/refresh Response")
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToUnauthorizedException_when_GivenSecurityUserItemAndUserIsNotAuthenticated() {
      mockMvc
        .perform(
          MockMvcRequestBuilders
            .post("/api/v1/auth/refresh")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }
  }
}
