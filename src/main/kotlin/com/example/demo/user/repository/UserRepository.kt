package com.example.demo.user.repository

import com.example.demo.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>, CustomUserRepository {
  fun findOneById(userId: Long): User?

  fun findOneByEmail(email: String): User?

  fun existsByEmail(email: String): Boolean
}
