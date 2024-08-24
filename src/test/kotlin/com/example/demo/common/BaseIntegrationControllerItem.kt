package com.example.demo.common

import com.example.demo.utils.SwaggerUtils
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.context.WebApplicationContext

open class BaseIntegrationControllerItem {
  @Autowired
  protected lateinit var webApplicationContext: WebApplicationContext

  @Autowired
  protected lateinit var objectMapper: ObjectMapper

  @MockBean
  protected lateinit var swaggerUtils: SwaggerUtils

  protected lateinit var mockMvc: MockMvc

  /**
   * ResponseAdvice Status
   */
  protected val commonStatus: Int = HttpStatus.OK.value()

  /**
   * ResponseAdvice Message
   */
  protected val commonMessage: String = HttpStatus.OK.name
}
