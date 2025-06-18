package com.example.paymentsystem.dto

data class PaymentWebhookRequest(
    val orderUid: String,
    val amount: Long,
    val status: String
)
