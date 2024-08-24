package com.example.demo.security.exception

import com.example.demo.common.exception.UnAuthorizedException

class RefreshTokenNotFoundException : UnAuthorizedException {
  constructor() : super("Refresh Token Not Found")

  constructor(userId: Long?) : super(
    "Refresh Token Not Found userId = $userId"
  )

  constructor(email: String?) : super(
    "Refresh Token Not Found email = $email"
  )
}
