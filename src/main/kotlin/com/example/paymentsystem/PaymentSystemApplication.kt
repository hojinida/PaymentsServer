package com.example.paymentsystem

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@SpringBootApplication
class PaymentSystemApplication

fun main(args: Array<String>) {
    runApplication<PaymentSystemApplication>(*args)
}
