package com.example.demo.utils

import com.example.demo.common.response.ErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.io.IOException

object SecurityUtils {
  private val logger = LoggerFactory.getLogger(this::class.java)
  private val objectMapper = ObjectMapper()

  fun sendErrorResponse(
    httpServletRequest: HttpServletRequest,
    httpServletResponse: HttpServletResponse,
    exception: Exception,
    message: String = ""
  ) {
    val errorResponse = ErrorResponse.of(
      HttpStatus.UNAUTHORIZED.value(),
      message,
      exception.message ?: "Security Filter Error"
    )

    logger.error(
      "Security Filter sendErrorResponse - {} {} {} {}",
      httpServletRequest.method,
      httpServletRequest.requestURI,
      message,
      exception.message ?: "Security Filter Error"
    )

    with(httpServletResponse) {
      status = HttpStatus.UNAUTHORIZED.value()
      contentType = MediaType.APPLICATION_JSON_VALUE

      try {
        writer.write(objectMapper.writeValueAsString(errorResponse))
      } catch (ioException: IOException) {
        ioException.printStackTrace()
      }
    }
  }
}
