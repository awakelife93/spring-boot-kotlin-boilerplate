package com.example.demo.security.component.provider

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AuthProvider {
  @Value("\${auth.x-api-key}")
  lateinit var apiKey: String

  fun ignoreListDefaultEndpoints(): Array<String> {
    return arrayOf(
      "/api-docs/**",
      "/swagger-ui/**",
      "/swagger.html",
      "/h2-console/**",
    )
  }

  fun whiteListDefaultEndpoints(): Array<String> {
    return arrayOf(
      "/api/v1/auth/signIn",
      "/api/v1/users/register",
    )
  }

  fun generateRequestAPIKey(request: HttpServletRequest): String? {
    return request.getHeader("X-API-KEY")
  }

  fun validateApiKey(requestAPIKey: String): Boolean {
    return apiKey == requestAPIKey
  }
}
