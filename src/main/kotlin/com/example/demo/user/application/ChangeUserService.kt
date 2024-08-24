package com.example.demo.user.application

import com.example.demo.user.dto.serve.request.CreateUserRequest
import com.example.demo.user.dto.serve.request.UpdateUserRequest
import com.example.demo.user.dto.serve.response.CreateUserResponse
import com.example.demo.user.dto.serve.response.UpdateMeResponse
import com.example.demo.user.dto.serve.response.UpdateUserResponse

interface ChangeUserService {
  fun updateUser(
    userId: Long,
    updateUserRequest: UpdateUserRequest
  ): UpdateUserResponse

  fun updateMe(
    userId: Long,
    updateUserRequest: UpdateUserRequest
  ): UpdateMeResponse

  fun createUser(createUserRequest: CreateUserRequest): CreateUserResponse

  fun deleteUser(userId: Long)
}
