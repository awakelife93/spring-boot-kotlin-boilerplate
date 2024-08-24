package com.example.demo.security.component.filter

import com.example.demo.security.component.provider.JWTProvider
import com.example.demo.utils.SecurityUtils
import io.jsonwebtoken.ExpiredJwtException
import io.micrometer.common.lang.NonNull
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class JWTAuthFilter(
  private val jwtProvider: JWTProvider
) : OncePerRequestFilter() {

  @Throws(IOException::class, ServletException::class)
  override fun doFilterInternal(
    @NonNull httpServletRequest: HttpServletRequest,
    @NonNull httpServletResponse: HttpServletResponse,
    @NonNull filterChain: FilterChain
  ) {
    try {
      jwtProvider.generateRequestToken(httpServletRequest)?.let {
        if (jwtProvider.validateToken(it)) {
          val usernamePasswordAuthenticationToken: UsernamePasswordAuthenticationToken = jwtProvider.getAuthentication(
            it
          )

          SecurityContextHolder
            .getContext().authentication = usernamePasswordAuthenticationToken
        } else {
          SecurityContextHolder.clearContext()
        }
      } ?: SecurityContextHolder.clearContext()

      filterChain.doFilter(httpServletRequest, httpServletResponse)
    } catch (exception: Exception) {
      SecurityContextHolder.clearContext()
      var message = "Invalid Token"

      if (exception is ExpiredJwtException) {
        message = "Expired Token"
      }

      SecurityUtils.sendErrorResponse(
        httpServletRequest,
        httpServletResponse,
        exception,
        message
      )
    }
  }
}
