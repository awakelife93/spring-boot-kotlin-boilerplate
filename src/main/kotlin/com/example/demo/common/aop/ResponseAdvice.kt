package com.example.demo.common.aop

import com.example.demo.common.response.ErrorResponse
import com.example.demo.common.response.OkResponse
import com.example.demo.utils.SwaggerUtils
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.lang.Nullable
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@RestControllerAdvice
class ResponseAdvice(
  private val swaggerUtils: SwaggerUtils
) : ResponseBodyAdvice<Any> {


  override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
    return MappingJackson2HttpMessageConverter::class.java.isAssignableFrom(
      converterType
    )
  }

  override fun beforeBodyWrite(
    @Nullable body: Any?,
    returnType: MethodParameter,
    selectedContentType: MediaType,
    selectedConverterType: Class<out HttpMessageConverter<*>>,
    request: ServerHttpRequest,
    response: ServerHttpResponse
  ): Any? {
    if (swaggerUtils.confirmPathEqualsSwaggerConfig(request.uri.path)
    ) {
      return body
    }

    return when (body) {
      is ErrorResponse -> body
      else -> body.let { OkResponse.of(HttpStatus.OK.value(), HttpStatus.OK.name, it) }
    }
  }
}
