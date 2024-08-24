package com.example.demo.common.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.util.*

@Component
class LoggingForInterceptor : HandlerInterceptor {
  val logger: Logger = LoggerFactory.getLogger(this::class.java)

  @Throws(Exception::class)
  override fun postHandle(
    request: HttpServletRequest,
    response: HttpServletResponse,
    handler: Any,
    modelAndView: ModelAndView?
  ) {
    logger.info(
      "{} {} {} - status: {}",
      UUID.randomUUID().toString(),
      request.requestURI,
      handler,
      response.status
    )

    super.postHandle(request, response, handler, null)
  }
}
