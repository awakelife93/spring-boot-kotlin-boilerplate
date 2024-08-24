package com.example.demo.user.repository

import com.example.demo.common.config.JpaAuditConfig
import com.example.demo.common.config.QueryDslConfig
import com.example.demo.user.constant.UserRole
import com.example.demo.user.dto.serve.request.UpdateUserRequest
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
@DisplayName("Unit - User Repository Test")
@Import(value = [QueryDslConfig::class, JpaAuditConfig::class])
@DataJpaTest
class UserRepositoryTests(
  @Autowired private val userRepository: UserRepository,
) {
  private val defaultUserEmail = "awakelife93@gmail.com"

  private val defaultUserPassword = "test_password_123!@"

  private val defaultUserName = "Hyunwoo Park"

  private val defaultUserRole = UserRole.USER

  private lateinit var userEntity: User

  @BeforeEach
  @Throws(Exception::class)
  fun setUp() {
    userEntity =
      User(
        defaultUserEmail,
        defaultUserName,
        defaultUserPassword,
        defaultUserRole
      )
  }

  @Test
  @DisplayName("Create user")
  fun should_AssertCreatedUserEntity_when_GivenUserEntity() {
    val createUser = userRepository.save(userEntity)

    assertEquals(createUser.id, userEntity.id)
    assertEquals(createUser.email, userEntity.email)
    assertEquals(createUser.name, userEntity.name)
    assertEquals(createUser.role, userEntity.role)
  }

  @Test
  @DisplayName("Update user")
  fun should_AssertUpdatedUserEntity_when_GivenUserIdAndUpdateUserRequest() {
    val updateUserRequest = Instancio.create(
      UpdateUserRequest::class.java
    )

    val beforeUpdateUser = userRepository.save(userEntity)

    userRepository.save(
      beforeUpdateUser.update(
        updateUserRequest.name,
        updateUserRequest.role
      )
    )

    val afterUpdateUser: User = requireNotNull(
      userRepository
        .findOneById(beforeUpdateUser.id)
    ) {
      "User must not be null"
    }

    assertEquals(afterUpdateUser.name, updateUserRequest.name)
    assertEquals(afterUpdateUser.role, updateUserRequest.role)
  }

  @Test
  @DisplayName("Delete user")
  fun should_AssertDeletedUserEntity_when_GivenUserId() {
    val beforeDeleteUser = userRepository.save(userEntity)

    userRepository.deleteById(beforeDeleteUser.id)

    val afterDeleteUser: User? = userRepository
      .findOneById(beforeDeleteUser.id)

    assertNull(afterDeleteUser)
  }

  @Test
  @DisplayName("Find user by id")
  fun should_AssertFindUserEntity_when_GivenUserId() {
    val beforeFindUser = userRepository.save(userEntity)

    val afterFindUser: User = requireNotNull(
      userRepository
        .findOneById(beforeFindUser.id)
    ) {
      "User must not be null"
    }

    assertEquals(beforeFindUser.id, afterFindUser.id)
    assertEquals(beforeFindUser.email, afterFindUser.email)
    assertEquals(beforeFindUser.name, afterFindUser.name)
    assertEquals(beforeFindUser.role, afterFindUser.role)
  }

  @Test
  @DisplayName("Find user by email")
  fun should_AssertFindUserEntity_when_GivenUserEmail() {
    val beforeFindUser = userRepository.save(userEntity)

    val afterFindUser: User = requireNotNull(
      userRepository
        .findOneByEmail(beforeFindUser.email)
    ) {
      "User must not be null"
    }

    assertEquals(beforeFindUser.id, afterFindUser.id)
    assertEquals(beforeFindUser.email, afterFindUser.email)
    assertEquals(beforeFindUser.name, afterFindUser.name)
    assertEquals(beforeFindUser.role, afterFindUser.role)
  }
}
