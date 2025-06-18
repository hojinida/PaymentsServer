package com.example.paymentsystem.client

import com.example.paymentsystem.config.AppProperties
import com.example.paymentsystem.dto.PaymentWebhookRequest
import com.example.paymentsystem.service.SignatureService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class WebhookClient(
    private val signatureService: SignatureService,
    private val objectMapper: ObjectMapper,
    appProperties: AppProperties
) {
    private val webClient = WebClient.builder().baseUrl(appProperties.webhookUrl).build()

    fun sendWebhook(uri: String, payload: PaymentWebhookRequest) {
        val payloadJson = objectMapper.writeValueAsString(payload)
        val signature = signatureService.generate(payloadJson)

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
