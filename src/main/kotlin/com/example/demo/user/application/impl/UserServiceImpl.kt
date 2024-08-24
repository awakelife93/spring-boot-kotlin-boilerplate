package com.example.demo.user.application.impl

import com.example.demo.auth.dto.serve.request.SignInRequest
import com.example.demo.user.application.UserService
import com.example.demo.user.entity.User
import com.example.demo.user.exception.UserNotFoundException
import com.example.demo.user.exception.UserUnAuthorizedException
import com.example.demo.user.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
  private val userRepository: UserRepository,
  private val bCryptPasswordEncoder: BCryptPasswordEncoder
) : UserService {

  override fun validateReturnUser(userId: Long): User {
    val user: User = userRepository
      .findOneById(userId) ?: throw UserNotFoundException(userId)

    return user
  }

  override fun validateAuthReturnUser(signInRequest: SignInRequest): User {
    val user: User = userRepository
      .findOneByEmail(signInRequest.email) ?: throw UserNotFoundException(signInRequest.email)

    val isValidate = user.validatePassword(
      signInRequest.password,
      bCryptPasswordEncoder
    )

    if (!isValidate) {
      throw UserUnAuthorizedException(signInRequest.email)
    }

    return user
  }
}
