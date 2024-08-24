package com.example.demo.utils

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.TimeUnit

@Component
class RedisUtils(
  private val stringRedisTemplate: StringRedisTemplate
) {
  fun get(key: String): String? {
    val valueOperations: ValueOperations<String, String> = stringRedisTemplate.opsForValue()
    return valueOperations.get(key)
  }

  fun set(key: String, value: String, duration: Long, timeUnit: TimeUnit) {
    val valueOperations: ValueOperations<String, String> = stringRedisTemplate.opsForValue()
    val expireDuration = Duration.ofSeconds(duration).seconds

    valueOperations.set(key, value, expireDuration, timeUnit)
  }

  fun delete(key: String) {
    stringRedisTemplate.delete(key)
  }

  fun generateSessionKey(userId: Long): String {
    val prefix = "Session_"
    return prefix + userId
  }
}
