package com.example.demo.security.component

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerExceptionResolver
import java.io.IOException

@Component
class CustomAuthenticationEntryPoint(
  @param:Qualifier("handlerExceptionResolver")
  private val resolver: HandlerExceptionResolver
) : AuthenticationEntryPoint {
  @Throws(ServletException::class, IOException::class)
  override fun commence(
    request: HttpServletRequest,
    response: HttpServletResponse,
    authException: AuthenticationException
  ) {
    resolver.resolveException(request, response, null, authException)
  }
}
