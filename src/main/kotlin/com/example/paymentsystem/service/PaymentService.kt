package com.example.paymentsystem.service


import com.example.paymentsystem.client.WebhookClient
import com.example.paymentsystem.dto.PaymentRequest
import com.example.paymentsystem.dto.PaymentType
import com.example.paymentsystem.dto.PaymentWebhookRequest
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class PaymentService(
    private val idempotencyService: IdempotencyService, private val webhookClient: WebhookClient
) {

    @Async
    fun processPayment(request: PaymentRequest) {
        processTransaction(request, PaymentType.PAYMENT)
    }

    @Async
    fun processCancellation(request: PaymentRequest) {
        processTransaction(request, PaymentType.CANCELLATION)
    }

    private fun processTransaction(request: PaymentRequest, type: PaymentType) {
        val result = idempotencyService.processWithIdempotency(request.idempotencyKey) {
            executeBusinessLogic(request)
        }

        webhookClient.sendWebhook(type.webhookEndpoint, result)
    }

    private fun executeBusinessLogic(request: PaymentRequest): PaymentWebhookRequest {
        Thread.sleep(2000)
        val isSuccess = Random.nextDouble() < 0.9
        val status = if (isSuccess) "SUCCESS" else "FAILURE"

        return PaymentWebhookRequest(
            orderUid = request.orderUid, amount = request.amount, status = status
        )
    }
}
