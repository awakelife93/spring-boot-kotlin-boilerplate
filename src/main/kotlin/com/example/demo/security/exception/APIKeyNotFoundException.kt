package com.example.demo.security.exception

import com.example.demo.common.exception.UnAuthorizedException

class APIKeyNotFoundException(requestURI: String?) : UnAuthorizedException(
  "API Key Not Found requestURI = $requestURI"
) {
}
