package com.example.paymentsystem.service

import com.example.paymentsystem.config.AppProperties
import org.springframework.stereotype.Service
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Service
class SignatureService(appProperties: AppProperties) {
    private val algorithm = "HmacSHA256"
    private val secretKeySpec = SecretKeySpec(appProperties.secretKey.toByteArray(), algorithm)
    fun generate(data: String): String {
        val mac = Mac.getInstance(algorithm)
        mac.init(secretKeySpec)
        return Base64.getEncoder().encodeToString(mac.doFinal(data.toByteArray()))
    }
    fun verify(data: String, signature: String): Boolean = generate(data) == signature
}
