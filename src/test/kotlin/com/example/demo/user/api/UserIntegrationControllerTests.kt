package com.example.demo.user.api

import com.example.demo.common.security.SecurityItem
import com.example.demo.common.security.WithMockCustomUser
import com.example.demo.user.application.impl.ChangeUserServiceImpl
import com.example.demo.user.application.impl.GetUserServiceImpl
import com.example.demo.user.dto.serve.request.CreateUserRequest
import com.example.demo.user.dto.serve.request.UpdateUserRequest
import com.example.demo.user.dto.serve.response.CreateUserResponse.Companion.of
import com.example.demo.user.dto.serve.response.GetUserResponse
import com.example.demo.user.dto.serve.response.UpdateMeResponse
import com.example.demo.user.dto.serve.response.UpdateUserResponse
import com.example.demo.user.entity.User
import com.example.demo.user.exception.AlreadyUserExistException
import com.example.demo.user.exception.UserNotFoundException
import org.instancio.Instancio
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
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
@DisplayName("integration - User Controller Test")
@WebMvcTest(
  UserController::class
)
@ExtendWith(MockitoExtension::class)
class UserIntegrationControllerTests : SecurityItem() {
  private val user: User = Instancio.create(User::class.java)

  @MockBean
  private lateinit var getUserServiceImpl: GetUserServiceImpl

  @MockBean
  private lateinit var changeUserServiceImpl: ChangeUserServiceImpl

  private val defaultUserEmail = "awakelife93@gmail.com"

  private val defaultUserPassword = "test_password_123!@"

  private val defaultAccessToken =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c\n" +  //
      ""

  @BeforeEach
  fun setup() {
    mockMvc =
      MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
        .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
        .build()
  }

  @Nested
  @DisplayName("GET /api/v1/users/{userId} Test")
  inner class GetUserByIdTest {
    @Test
    @DisplayName("GET /api/v1/users/{userId} Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectOKResponseToGetUserResponse_when_GivenUserIdAndUserIsAuthenticated() {
      Mockito.`when`(getUserServiceImpl.getUserById(any<Long>()))
        .thenReturn(GetUserResponse.of(user))

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .get("/api/v1/users/{userId}", user.id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(commonMessage))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.userId").value(user.id))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value(user.email))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(user.name))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.role").value(user.role.name))
    }

    @Test
    @DisplayName("Not Found Exception GET /api/v1/users/{userId} Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToUserNotFoundException_when_GivenUserIdAndUserIsAuthenticated() {
      val userNotFoundException = UserNotFoundException(
        user.id
      )

      Mockito.`when`(getUserServiceImpl.getUserById(any<Long>()))
        .thenThrow(userNotFoundException)

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .get("/api/v1/users/{userId}", user.id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(
          MockMvcResultMatchers.jsonPath("$.message").value(userNotFoundException.message)
        )
        .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isEmpty())
    }

    @Test
    @DisplayName("Unauthorized Exception GET /api/v1/users/{userId} Response")
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToUnauthorizedException_when_GivenUserIdAndUserIsNotAuthenticated() {
      mockMvc
        .perform(
          MockMvcRequestBuilders
            .get("/api/v1/users/{userId}", user.id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }
  }

  @Nested
  @DisplayName("GET /api/v1/users Test")
  inner class GetUserListTest {
    @Test
    @DisplayName("GET /api/v1/users Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectOKResponseToListOfGetUserResponse_when_GivenDefaultPageableAndUserIsAuthenticated() {
      Mockito.`when`(getUserServiceImpl.getUserList(any<Pageable>()))
        .thenReturn(listOf(GetUserResponse.of(user)))

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .get("/api/v1/users")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(commonMessage))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.[0].userId").value(user.id))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.[0].email").value(user.email))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.[0].name").value(user.name))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.[0].role").value(user.role.name))
    }

    @Test
    @DisplayName("Empty GET /api/v1/users Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectOKResponseToListOfGetUserResponseIsEmpty_when_GivenDefaultPageableAndUserIsAuthenticated() {
      Mockito.`when`(getUserServiceImpl.getUserList(any<Pageable>()))
        .thenReturn(listOf())

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .get("/api/v1/users")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(commonMessage))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @Test
    @DisplayName("Unauthorized Exception GET /api/v1/users Response")
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToUnauthorizedException_when_GivenDefaultPageableAndUserIsNotAuthenticated() {
      mockMvc
        .perform(
          MockMvcRequestBuilders
            .get("/api/v1/users")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }
  }

  @Nested
  @DisplayName("POST /api/v1/users/register Test")
  inner class CreateUserTest {
    private val mockCreateUserRequest: CreateUserRequest = Instancio.create(
      CreateUserRequest::class.java
    )
    private val createUserRequest: CreateUserRequest = mockCreateUserRequest.copy(
      email = defaultUserEmail,
      password = defaultUserPassword
    )

    @Test
    @DisplayName("POST /api/v1/users/register Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectOKResponseToCreateUserResponse_when_GivenCreateUserRequest() {
      Mockito.`when`(changeUserServiceImpl.createUser(any<CreateUserRequest>()))
        .thenReturn(of(user, defaultAccessToken))

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .post("/api/v1/users/register")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(createUserRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(commonStatus))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(commonMessage))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.userId").value(user.id))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value(user.email))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(user.name))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.role").value(user.role.name))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").value(defaultAccessToken))
    }

    @Test
    @DisplayName("Field Valid Exception POST /api/v1/users/register Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToValidException_when_GivenWrongCreateUserRequest() {
      val wrongCreateUserRequest = createUserRequest.copy(
        email = "wrong_email",
        password = "1234567"
      )

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .post("/api/v1/users/register")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(wrongCreateUserRequest))
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
    @DisplayName("Already Exist Exception POST /api/v1/users/register Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToAlreadyUserExistException_when_GivenCreateUserRequest() {
      val alreadyUserExistException = AlreadyUserExistException(
        createUserRequest.email
      )

      Mockito.`when`(changeUserServiceImpl.createUser(any<CreateUserRequest>()))
        .thenThrow(alreadyUserExistException)

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .post("/api/v1/users/register")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(createUserRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isConflict())
        .andExpect(
          MockMvcResultMatchers.jsonPath("$.message").value(alreadyUserExistException.message)
        )
        .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isEmpty())
    }
  }

  @Nested
  @DisplayName("PATCH /api/v1/users/{userId} Test")
  inner class UpdateUserTest {
    private val updateUserRequest: UpdateUserRequest = Instancio.create(
      UpdateUserRequest::class.java
    )

    @Test
    @DisplayName("PATCH /api/v1/users/{userId} Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectOKResponseToUpdateUserResponse_when_GivenUserIdAndUpdateUserRequestAndUserIsAuthenticated() {
      Mockito.`when`(changeUserServiceImpl.updateUser(any<Long>(), any<UpdateUserRequest>()))
        .thenReturn(UpdateUserResponse.of(user))

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .patch("/api/v1/users/{userId}", user.id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(updateUserRequest))
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
    @DisplayName("Field Valid Exception PATCH /api/v1/users/{userId} Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToValidException_when_GivenUserIdAndWrongUpdateUserRequestAndUserIsAuthenticated() {
      val wrongUpdateUserRequest = updateUserRequest.copy(
        name = "",
      )

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .patch("/api/v1/users/{userId}", user.id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(wrongUpdateUserRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        // name:field name is blank
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").isString())
        .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isNotEmpty())
    }

    @Test
    @DisplayName("Unauthorized Exception PATCH /api/v1/users/{userId} Response")
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToUnauthorizedException_when_GivenUserIdAndUpdateUserRequestAndUserIsNotAuthenticated() {
      mockMvc
        .perform(
          MockMvcRequestBuilders
            .patch("/api/v1/users/{userId}", user.id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(updateUserRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }

    @Test
    @DisplayName("Not Found Exception PATCH /api/v1/users/{userId} Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToUserNotFoundException_when_GivenUserIdAndUpdateUserRequestAndUserIsAuthenticated() {
      val userNotFoundException = UserNotFoundException(
        user.id
      )

      Mockito.`when`(changeUserServiceImpl.updateUser(any<Long>(), any<UpdateUserRequest>()))
        .thenThrow(userNotFoundException)

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .patch("/api/v1/users/{userId}", user.id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(updateUserRequest))
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
  @DisplayName("PATCH /api/v1/users Test")
  inner class UpdateMeTest {
    private val updateUserRequest: UpdateUserRequest = Instancio.create(
      UpdateUserRequest::class.java
    )

    @Test
    @DisplayName("PATCH /api/v1/users Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectOKResponseToUpdateMeResponse_when_GivenSecurityUserItemAndUpdateUserRequestAndUserIsAuthenticated() {
      Mockito.`when`(changeUserServiceImpl.updateMe(any<Long>(), any<UpdateUserRequest>()))
        .thenReturn(UpdateMeResponse.of(user, defaultAccessToken))

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .patch("/api/v1/users")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(updateUserRequest))
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
    @DisplayName("Field Valid Exception PATCH /api/v1/users Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToValidException_when_GivenSecurityUserItemAndWrongUpdateUserRequestAndUserIsAuthenticated() {
      val wrongUpdateUserRequest = updateUserRequest.copy(
        name = "",
      )

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .patch("/api/v1/users")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(wrongUpdateUserRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(
          MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value())
        ) // name:field name is blank
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").isString())
        .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isNotEmpty())
    }

    @Test
    @DisplayName("Unauthorized Exception PATCH /api/v1/users Response")
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToUnauthorizedException_when_GivenUpdateUserRequestAndUserIsNotAuthenticated() {
      mockMvc
        .perform(
          MockMvcRequestBuilders
            .patch("/api/v1/users")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(updateUserRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }

    @Test
    @DisplayName("Not Found Exception PATCH /api/v1/users Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToUserNotFoundException_when_GivenSecurityUserItemAndUpdateUserRequestAndUserIsAuthenticated() {
      val userNotFoundException = UserNotFoundException(
        user.id
      )

      Mockito.`when`(changeUserServiceImpl.updateMe(any<Long>(), any<UpdateUserRequest>()))
        .thenThrow(userNotFoundException)

      mockMvc
        .perform(
          MockMvcRequestBuilders
            .patch("/api/v1/users")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(objectMapper.writeValueAsString(updateUserRequest))
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
  @DisplayName("DELETE /api/v1/users/{userId} Test")
  inner class DeleteUserTest {
    @Test
    @DisplayName("DELETE /api/v1/users/{userId} Response")
    @WithMockCustomUser
    @Throws(
      Exception::class
    )
    fun should_ExpectOKResponse_when_GivenUserIdAndUserIsAuthenticated() {
      mockMvc
        .perform(
          MockMvcRequestBuilders
            .delete("/api/v1/users/{userId}", user.id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(commonMessage))
    }

    @Test
    @DisplayName("Unauthorized Error DELETE /api/v1/users/{userId} Response")
    @Throws(
      Exception::class
    )
    fun should_ExpectErrorResponseToUnauthorizedException_when_GivenUserIdAndUserIsNotAuthenticated() {
      mockMvc
        .perform(
          MockMvcRequestBuilders
            .delete("/api/v1/users/{userId}", user.id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }
  }
}
