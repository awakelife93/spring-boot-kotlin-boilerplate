package com.example.demo.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {
  @Value("\${spring.data.redis.port}")
  var port: Int = 0

  @Value("\${spring.data.redis.host}")
  lateinit var host: String

  @Bean
  fun redisConnectionFactory(): LettuceConnectionFactory = LettuceConnectionFactory(
    RedisStandaloneConfiguration(host, port)
  )

  @Bean
  fun redisTemplate(
    redisConnectionFactory: RedisConnectionFactory?
  ): RedisTemplate<String, Any> {
    val redisTemplate = RedisTemplate<String, Any>()

    redisTemplate.keySerializer = StringRedisSerializer()
    redisTemplate.valueSerializer = GenericJackson2JsonRedisSerializer()

    redisTemplate.connectionFactory = redisConnectionFactory
    return redisTemplate
  }

  @Bean
  fun stringRedisTemplate(
    redisConnectionFactory: RedisConnectionFactory?
  ): StringRedisTemplate {
    val stringRedisTemplate = StringRedisTemplate()
    stringRedisTemplate.connectionFactory = redisConnectionFactory
    return stringRedisTemplate
  }
}
