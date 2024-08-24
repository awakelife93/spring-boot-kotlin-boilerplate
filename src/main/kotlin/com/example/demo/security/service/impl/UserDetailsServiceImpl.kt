package com.example.demo.security.service.impl

import com.example.demo.security.SecurityUserItem
import com.example.demo.security.UserAdapter
import com.example.demo.user.application.UserService
import com.example.demo.user.entity.User
import com.example.demo.user.exception.UserNotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
  private val userService: UserService
) : UserDetailsService {

  @Throws(UserNotFoundException::class)
  override fun loadUserByUsername(userId: String): UserDetails {
    val user: User = userService.validateReturnUser(userId.toLong())

    return UserAdapter(SecurityUserItem.of(user))
  }
}
