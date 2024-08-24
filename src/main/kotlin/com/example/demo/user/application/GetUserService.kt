package com.example.demo.user.application;

import com.example.demo.user.dto.serve.response.GetUserResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetUserService {
  fun getUserById(userId: Long): GetUserResponse

  fun getUserByEmail(email: String): GetUserResponse?

  fun getUserList(pageable: Pageable): Page<GetUserResponse>
}
