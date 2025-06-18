package com.example.paymentsystem.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app")
data class AppProperties(
    var webhookUrl: String = "", var secretKey: String = ""
)
