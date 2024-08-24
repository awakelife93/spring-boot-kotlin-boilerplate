package com.example.demo.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
open class UnAuthorizedException(message: String) : CustomRuntimeException(message)

