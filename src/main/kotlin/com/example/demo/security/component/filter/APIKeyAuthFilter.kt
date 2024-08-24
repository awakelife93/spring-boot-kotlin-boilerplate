package com.example.demo.security.component.filter

import com.example.demo.security.component.provider.AuthProvider
import com.example.demo.security.exception.APIKeyNotFoundException
import com.example.demo.utils.SecurityUtils
import io.micrometer.common.lang.NonNull
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class APIKeyAuthFilter(
  private val authProvider: AuthProvider
) : OncePerRequestFilter() {

  @Throws(IOException::class, ServletException::class)
  override fun doFilterInternal(
    @NonNull httpServletRequest: HttpServletRequest,
    @NonNull httpServletResponse: HttpServletResponse,
    @NonNull filterChain: FilterChain
  ) {
    try {
      authProvider.generateRequestAPIKey(httpServletRequest)?.let {
        if (!authProvider.validateApiKey(it)) {
          throw APIKeyNotFoundException(httpServletRequest.requestURI)
        }
      } ?: throw APIKeyNotFoundException(httpServletRequest.requestURI)

      filterChain.doFilter(httpServletRequest, httpServletResponse)
    } catch (exception: APIKeyNotFoundException) {
      SecurityUtils.sendErrorResponse(
        httpServletRequest,
        httpServletResponse,
        exception,
      )
    }
  }
}
