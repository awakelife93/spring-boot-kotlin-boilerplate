package com.example.demo.user.repository.impl

import com.example.demo.user.repository.CustomUserRepository
import com.querydsl.jpa.impl.JPAQueryFactory

class CustomUserRepositoryImpl(
  private val jpaQueryFactory: JPAQueryFactory
) : CustomUserRepository {
  // Implement custom methods here
}
