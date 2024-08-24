package com.example.demo.user.application;

import com.example.demo.auth.dto.serve.request.SignInRequest;
import com.example.demo.user.entity.User;

interface UserService {
  fun validateReturnUser(userId: Long): User

  fun validateAuthReturnUser(signInRequest: SignInRequest): User
}
