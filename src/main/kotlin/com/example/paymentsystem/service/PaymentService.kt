package com.example.paymentsystem.service


import com.example.paymentsystem.config.AppProperties
import com.example.paymentsystem.dto.CancelWebhookRequest
import com.example.paymentsystem.dto.PaymentCancellationRequest
import com.example.paymentsystem.dto.PaymentConfirmRequest
import com.example.paymentsystem.dto.PaymentWebhookRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import kotlin.random.Random

@Service
class PaymentService(
    private val signatureService: SignatureService,
    private val objectMapper: ObjectMapper,
    appProperties: AppProperties
) {
    private val webClient = WebClient.builder().baseUrl(appProperties.webhookUrl).build()

    @Async
    fun processPayment(request: PaymentConfirmRequest) {
        Thread.sleep(2000)

        val isSuccess = Random.nextDouble() < 0.9
        val status = if (isSuccess) "SUCCESS" else "FAILURE"
        val webhookPayload = PaymentWebhookRequest(
            idempotencyKey = request.idempotencyKey,
            orderUid = request.orderUid,
            amount = request.amount,
            status = status
        )

        val payloadJson = objectMapper.writeValueAsString(webhookPayload)
        val signature = signatureService.generate(payloadJson)

        sendWebhook("/api/webhooks/payment", payloadJson, signature)
    }

    @Async
    fun processCancellation(request: PaymentCancellationRequest) {
        Thread.sleep(2000)

        val isSuccess = Random.nextDouble() < 0.9
        val status = if (isSuccess) "SUCCESS" else "FAILURE"

        val webhookPayload = CancelWebhookRequest(
            idempotencyKey = request.idempotencyKey,
            orderUid = request.orderUid,
            amount = request.amount,
            status = status
        )

        val payloadJson = objectMapper.writeValueAsString(webhookPayload)
        val signature = signatureService.generate(payloadJson)
        sendWebhook("/api/webhooks/cancel", payloadJson, signature)
    }

    private fun sendWebhook(uri: String, payloadJson: String, signature: String) {
        webClient.post()
            .uri(uri)
            .header("Content-Type", "application/json")
            .header("X-Signature", signature)
            .bodyValue(payloadJson)
            .retrieve()
            .toBodilessEntity()
            .subscribe()
    }
}
