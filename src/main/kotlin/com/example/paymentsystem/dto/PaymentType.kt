package com.example.paymentsystem.dto

enum class PaymentType(val webhookEndpoint: String) {
    PAYMENT("/api/webhooks/payment"),
    CANCELLATION("/api/webhooks/cancel")
}
