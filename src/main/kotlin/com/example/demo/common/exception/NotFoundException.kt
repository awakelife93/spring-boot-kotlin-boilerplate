package com.example.demo.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
open class NotFoundException(message: String) : CustomRuntimeException(message)
