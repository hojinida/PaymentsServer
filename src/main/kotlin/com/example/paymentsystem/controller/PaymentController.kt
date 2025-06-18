package com.example.paymentsystem.controller

import com.example.paymentsystem.dto.PaymentRequest
import com.example.paymentsystem.service.PaymentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/pay")
class PaymentController(
    private val paymentService: PaymentService
) {

    @PostMapping("/confirm")
    fun confirmPayment(@RequestBody request: PaymentRequest): ResponseEntity<String> {
        paymentService.processPayment(request)
        return ResponseEntity.ok("결제 요청 접수 완료")
    }

    @PostMapping("/cancel")
    fun cancelPayment(@RequestBody request: PaymentRequest): ResponseEntity<String> {
        paymentService.processCancellation(request)
        return ResponseEntity.ok("취소 요청 접수 완료")
    }
}
