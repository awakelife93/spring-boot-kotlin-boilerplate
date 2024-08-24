package com.example.demo.common.exception.handler

import com.example.demo.common.exception.AlreadyExistException
import com.example.demo.common.exception.CustomRuntimeException
import com.example.demo.common.exception.NotFoundException
import com.example.demo.common.exception.UnAuthorizedException
import com.example.demo.common.response.ErrorResponse
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
class ErrorHandler {
  var logger: Logger = LoggerFactory.getLogger(this::class.java)

  @ExceptionHandler(CustomRuntimeException::class)
  protected fun handleCustomRuntimeException(
    exception: CustomRuntimeException,
    httpServletRequest: HttpServletRequest
  ): ResponseEntity<ErrorResponse> {
    val response = ErrorResponse.of(
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      exception.message ?: "Internal Server Error"
    )

    logger.error(
      "handleCustomRuntimeException Error - {} {} {}",
      httpServletRequest.method,
      httpServletRequest.requestURI,
      exception.message
    )

    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(response)
  }

  @ExceptionHandler(ExpiredJwtException::class)
  protected fun handleExpiredJwtException(
    exception: ExpiredJwtException,
    httpServletRequest: HttpServletRequest
  ): ResponseEntity<ErrorResponse> {
    val response = ErrorResponse.of(
      HttpStatus.UNAUTHORIZED.value(),
      exception.message ?: "Expired JWT"
    )

    logger.error(
      "handleExpiredJwtException Error - {} {} {}",
      httpServletRequest.method,
      httpServletRequest.requestURI,
      exception.message
    )
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
  }

  @ExceptionHandler(NotFoundException::class)
  protected fun handleNotFoundException(
    exception: NotFoundException,
    httpServletRequest: HttpServletRequest
  ): ResponseEntity<ErrorResponse> {
    val response = ErrorResponse.of(
      HttpStatus.NOT_FOUND.value(),
      exception.message ?: "Not Found"
    )

    logger.error(
      "handleNotFoundException Error - {} {} {}",
      httpServletRequest.method,
      httpServletRequest.requestURI,
      exception.message
    )
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body<ErrorResponse>(response)
  }

  @ExceptionHandler(UnAuthorizedException::class)
  protected fun handleUnAuthorizedException(
    exception: UnAuthorizedException,
    httpServletRequest: HttpServletRequest
  ): ResponseEntity<ErrorResponse> {
    val response = ErrorResponse.of(
      HttpStatus.UNAUTHORIZED.value(),
      exception.message ?: "UnAuthorized"
    )

    logger.error(
      "handleUnAuthorizedException Error - {} {} {}",
      httpServletRequest.method,
      httpServletRequest.requestURI,
      exception.message
    )
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body<ErrorResponse>(response)
  }

  @ExceptionHandler(AlreadyExistException::class)
  protected fun handleAlreadyExistException(
    exception: AlreadyExistException,
    httpServletRequest: HttpServletRequest
  ): ResponseEntity<ErrorResponse> {
    val response = ErrorResponse.of(
      HttpStatus.CONFLICT.value(),
      exception.message ?: "Already Exist"
    )

    logger.error(
      "handleAlreadyExistException Error - {} {} {}",
      httpServletRequest.method,
      httpServletRequest.requestURI,
      exception.message
    )
    return ResponseEntity.status(HttpStatus.CONFLICT).body<ErrorResponse>(response)
  }

  @ExceptionHandler(BindException::class)
  protected fun handleMethodArgumentNotValidException(
    exception: BindException,
    httpServletRequest: HttpServletRequest
  ): ResponseEntity<ErrorResponse> {
    val bindingResult = exception.bindingResult
    val stringBuilder = StringBuilder()
    for (fieldError in bindingResult.fieldErrors) {
      stringBuilder.append(fieldError.field).append(":")
      stringBuilder.append(fieldError.defaultMessage)
      stringBuilder.append(", ")
    }

    val response = ErrorResponse.of(
      HttpStatus.BAD_REQUEST.value(),
      stringBuilder.toString(),
      exception.fieldErrors
    )

    logger.error(
      "handleMethodArgumentNotValidException Error - {} {} {}",
      httpServletRequest.method,
      httpServletRequest.requestURI,
      stringBuilder.toString()
    )
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
  }

  @ExceptionHandler(AuthenticationException::class)
  protected fun handleAuthenticationException(
    exception: AuthenticationException,
    httpServletRequest: HttpServletRequest
  ): ResponseEntity<ErrorResponse> {
    val response = ErrorResponse.of(
      HttpStatus.UNAUTHORIZED.value(),
      exception.message ?: "Authentication Error"
    )

    logger.error(
      "handleAuthenticationException Error - {} {} {}",
      httpServletRequest.method,
      httpServletRequest.requestURI,
      exception.message
    )
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
  }

  @ExceptionHandler(NoHandlerFoundException::class)
  protected fun handleNoHandlerFoundException(
    exception: NoHandlerFoundException,
    httpServletRequest: HttpServletRequest
  ): ResponseEntity<ErrorResponse> {
    val response = ErrorResponse.of(
      HttpStatus.NOT_FOUND.value(),
      exception.message ?: "No Handler Found",
      exception.requestHeaders
    )

    logger.error(
      "handleNoHandlerFoundException Error - {} {} {}",
      httpServletRequest.method,
      httpServletRequest.requestURI,
      exception.message
    )
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
  }

  @ExceptionHandler(Exception::class)
  protected fun handleException(
    exception: Exception,
    httpServletRequest: HttpServletRequest
  ): ResponseEntity<ErrorResponse> {
    val response = ErrorResponse.of(
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      exception.message ?: "Internal Server Error"
    )

    logger.error(
      "handleException Error - {} {} {}",
      httpServletRequest.method,
      httpServletRequest.requestURI,
      exception.message
    )
    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(response)
  }
}
