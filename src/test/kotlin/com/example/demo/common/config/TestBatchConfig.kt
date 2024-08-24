package com.example.demo.common.config

import com.example.demo.post.entity.Post
import com.example.demo.post.repository.PostRepository
import com.example.demo.user.entity.User
import com.example.demo.user.repository.UserRepository
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@TestConfiguration
@Import(QueryDslConfig::class)
@EnableAutoConfiguration
@EnableJpaRepositories(basePackageClasses = [UserRepository::class, PostRepository::class])
@EntityScan(basePackageClasses = [User::class, Post::class])
class TestBatchConfig
