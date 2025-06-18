package com.example.paymentsystem.dto

data class PaymentRequest(
    val orderUid: String,
    val amount: Long,
    val idempotencyKey: String
)
