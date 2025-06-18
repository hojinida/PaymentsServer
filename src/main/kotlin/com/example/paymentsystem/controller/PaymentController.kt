package com.example.paymentsystem.controller

import com.example.paymentsystem.dto.PaymentCancellationRequest
import com.example.paymentsystem.dto.PaymentConfirmRequest
import com.example.paymentsystem.service.PaymentService
import com.example.paymentsystem.service.SignatureService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/pay")
class PaymentController(
    private val paymentService: PaymentService,
    private val signatureService: SignatureService,
    private val objectMapper: ObjectMapper
) {

    @PostMapping("/confirm")
    fun confirmPayment(
        @RequestBody request: PaymentConfirmRequest,
        @RequestHeader("X-Signature") signature: String
    ): ResponseEntity<String> {
        val payloadJson = objectMapper.writeValueAsString(request)
        if (!signatureService.verify(payloadJson, signature)) {
            return ResponseEntity.status(403).body("Invalid Signature")
        }

        paymentService.processPayment(request)
        return ResponseEntity.ok("결제 요청 접수 완료")
    }

    @PostMapping("/cancel")
    fun cancelPayment(
        @RequestBody request: PaymentCancellationRequest,
        @RequestHeader("X-Signature") signature: String
    ): ResponseEntity<String> {
        val payloadJson = objectMapper.writeValueAsString(request)
        if (!signatureService.verify(payloadJson, signature)) {
            return ResponseEntity.status(403).body("Invalid Signature")
        }

        paymentService.processCancellation(request)
        return ResponseEntity.ok("취소 요청 접수 완료")
    }
}
