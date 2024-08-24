package com.example.demo.user.exception

import com.example.demo.common.exception.AlreadyExistException

class AlreadyUserExistException : AlreadyExistException {
  constructor(userId: Long) : super(
    "User Not Found userId = $userId"
  )

  constructor(email: String) : super(
    "User Not Found email = $email"
  )
}
