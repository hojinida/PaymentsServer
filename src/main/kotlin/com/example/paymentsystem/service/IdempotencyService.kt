package com.example.paymentsystem.service

import com.example.paymentsystem.dto.PaymentWebhookRequest
import com.example.paymentsystem.error.ConcurrentProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Service

@Service
class IdempotencyService(
    private val redisTemplate: RedisTemplate<String, String>, private val objectMapper: ObjectMapper
) {
    private val keyPrefix = "idempotency:"
    private val processingFlag = "PROCESSING"
    private val ttlSeconds = 300L // 5분

    // Lua 스크립트 초기화
    private val idempotencyScript = DefaultRedisScript<String>().apply {
        setLocation(ClassPathResource("scripts/idempotency.lua"))
        resultType = String::class.java
    }

    fun processWithIdempotency(
        idempotencyKey: String, operation: () -> PaymentWebhookRequest
    ): PaymentWebhookRequest {
        val redisKey = keyPrefix + idempotencyKey

        val scriptResult = redisTemplate.execute(
            idempotencyScript, listOf(redisKey), processingFlag, ttlSeconds.toString()
        )

        return when (scriptResult) {
            "ACQUIRED" -> {
                try {
                    val result = operation()
                    val resultJson = objectMapper.writeValueAsString(result)

                    redisTemplate.opsForValue().set(redisKey, resultJson)

                    return result
                } catch (e: Exception) {
                    redisTemplate.delete(redisKey)
                    throw e
                }
            }

            processingFlag -> {
                throw ConcurrentProcessingException(
                    "요청이 이미 처리 중입니다. 잠시 후 다시 시도해주세요."
                )
            }

            else -> {
                objectMapper.readValue(scriptResult, PaymentWebhookRequest::class.java)
            }
        }
    }
}
