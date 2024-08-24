package com.example.demo.user.application.impl

import com.example.demo.security.component.provider.TokenProvider
import com.example.demo.user.application.ChangeUserService
import com.example.demo.user.application.UserService
import com.example.demo.user.constant.UserRole
import com.example.demo.user.dto.serve.request.CreateUserRequest
import com.example.demo.user.dto.serve.request.UpdateUserRequest
import com.example.demo.user.dto.serve.response.CreateUserResponse
import com.example.demo.user.dto.serve.response.UpdateMeResponse
import com.example.demo.user.dto.serve.response.UpdateUserResponse
import com.example.demo.user.entity.User
import com.example.demo.user.exception.AlreadyUserExistException
import com.example.demo.user.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ChangeUserServiceImpl(
  private val tokenProvider: TokenProvider,
  private val userService: UserService,
  private val userRepository: UserRepository,
  private val bCryptPasswordEncoder: BCryptPasswordEncoder
) : ChangeUserService {

  override fun createUser(createUserRequest: CreateUserRequest): CreateUserResponse {
    val isExist: Boolean = userRepository.existsByEmail(createUserRequest.email)
    if (isExist) {
      throw AlreadyUserExistException(createUserRequest.email)
    }

    val user: User = User(
      name = createUserRequest.name,
      email = createUserRequest.email,
      password = createUserRequest.password,
      role = UserRole.USER
    ).encodePassword(bCryptPasswordEncoder)

    return CreateUserResponse.of(
      userRepository.save(user),
      tokenProvider.createFullTokens(user)
    )
  }

  override fun updateUser(
    userId: Long,
    updateUserRequest: UpdateUserRequest
  ): UpdateUserResponse {
    val user: User = userService
      .validateReturnUser(userId)
      .update(name = updateUserRequest.name, role = updateUserRequest.role)

    return user.let(UpdateUserResponse::of)
  }

  override fun updateMe(
    userId: Long,
    updateUserRequest: UpdateUserRequest
  ): UpdateMeResponse {
    val user: User = userService
      .validateReturnUser(userId)
      .update(name = updateUserRequest.name, role = updateUserRequest.role)

    return UpdateMeResponse.of(user, tokenProvider.createFullTokens(user))
  }

  override fun deleteUser(userId: Long) {
    tokenProvider.deleteRefreshToken(userId)
    userRepository.deleteById(userId)
  }
}
