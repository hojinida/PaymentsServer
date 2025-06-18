package com.example.paymentsystem.service

import com.example.paymentsystem.dto.PaymentWebhookRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class IdempotencyService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    private val keyPrefix = "idempotency:"

    fun processWithIdempotency(
        idempotencyKey: String,
        operation: () -> PaymentWebhookRequest
    ): PaymentWebhookRequest {
        val redisKey = keyPrefix + idempotencyKey

        val existingResult = redisTemplate.opsForValue().get(redisKey)
        if (existingResult != null) {
            return objectMapper.readValue(existingResult, PaymentWebhookRequest::class.java)
        }

        val result = operation()

        val resultJson = objectMapper.writeValueAsString(result)
        redisTemplate.opsForValue().set(redisKey, resultJson)

        return result
    }
}
