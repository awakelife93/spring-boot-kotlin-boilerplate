package com.example.demo.auth.application

import com.example.demo.auth.dto.serve.request.SignInRequest
import com.example.demo.auth.dto.serve.response.RefreshAccessTokenResponse
import com.example.demo.auth.dto.serve.response.SignInResponse
import com.example.demo.security.SecurityUserItem
import com.example.demo.security.component.provider.TokenProvider
import com.example.demo.user.application.UserService
import com.example.demo.user.entity.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuthService(
  private val userService: UserService,
  private val tokenProvider: TokenProvider
) {
  fun signIn(signInRequest: SignInRequest): SignInResponse {
    val user: User = userService.validateAuthReturnUser(signInRequest)

    return user.let {
      SignInResponse.of(it, tokenProvider.createFullTokens(it))
    }
  }

  fun signOut(userId: Long) {
    tokenProvider.deleteRefreshToken(userId)
    SecurityContextHolder.clearContext()
  }

  fun refreshAccessToken(
    securityUserItem: SecurityUserItem
  ): RefreshAccessTokenResponse = RefreshAccessTokenResponse.of(
    tokenProvider.refreshAccessToken(securityUserItem)
  )
}
