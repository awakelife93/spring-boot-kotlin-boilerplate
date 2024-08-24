package com.example.demo.utils

import com.example.demo.utils.SecurityUtils.sendErrorResponse
import org.instancio.Instancio
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ActiveProfiles
import java.io.PrintWriter
import java.io.StringWriter

@ActiveProfiles("test")
@Tag("unit-test")
@DisplayName("Unit - Security Utils Test")
@ExtendWith(
  MockitoExtension::class
)
class SecurityUtilsTest {
  @Mock
  private lateinit var mockHttpServletRequest: MockHttpServletRequest

  @Mock
  private lateinit var mockHttpServletResponse: MockHttpServletResponse

  @Test
  @DisplayName("Send Error Response Test")
  @Throws(Exception::class)
  fun should_VerifyCallMethodsOfHttpServletResponse_when_GivenServletAndException() {
    val exception = Instancio.create(Exception::class.java)
    val stringWriter = StringWriter()
    val printWriter = PrintWriter(stringWriter)

    Mockito.`when`(mockHttpServletResponse.writer).thenReturn(printWriter)

    sendErrorResponse(
      mockHttpServletRequest,
      mockHttpServletResponse,
      exception,
      "test exception"
    )

    Mockito.verify(mockHttpServletResponse, Mockito.times(1)).status = any<Int>()
    Mockito.verify(mockHttpServletResponse, Mockito.times(1)).contentType =
      any<String>()
    Mockito.verify(mockHttpServletResponse, Mockito.times(1)).writer
  }
}
