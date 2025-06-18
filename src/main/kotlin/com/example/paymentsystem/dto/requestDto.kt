package com.example.paymentsystem.dto

data class PaymentConfirmRequest(
    val orderUid: String,
    val amount: Long,
    val idempotencyKey: String
)

data class PaymentCancellationRequest(
    val orderUid: String,
    val amount: Long,
    val idempotencyKey: String
)

data class PaymentWebhookRequest(
    val idempotencyKey: String,
    val orderUid: String,
    val amount: Long,
    val status: String
)

data class CancelWebhookRequest(
    val idempotencyKey: String,
    val orderUid: String,
    val amount: Long,
    val status: String
)
