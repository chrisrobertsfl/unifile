package com.ingenifi.unifile

import io.ktor.client.*
import io.ktor.client.engine.java.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate

object UnsecuredHttpClient {

    fun create(): HttpClient {
        val sslContext = createInsecureSslContext()

        return HttpClient(Java) {
            engine {
                config {
                    sslContext(sslContext)
                    // Additional SSL parameters can be configured here if needed
                }
            }
        }
    }

    private fun createInsecureSslContext(): SSLContext {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        })

        return SSLContext.getInstance("TLS").apply {
            init(null, trustAllCerts, java.security.SecureRandom())
        }
    }
}
