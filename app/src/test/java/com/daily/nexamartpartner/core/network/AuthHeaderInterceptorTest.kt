package com.daily.nexamartpartner.core.network

import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthHeaderInterceptorTest {
    @Test
    fun `adds bearer token for protected requests`() {
        val interceptor = AuthHeaderInterceptor { "test-token" }
        val chain = CapturingChain("https://example.com/api/v1/delivery/dashboard")

        interceptor.intercept(chain)

        assertEquals("Bearer test-token", chain.capturedRequest.header("Authorization"))
    }

    @Test
    fun `does not add auth header for login endpoint`() {
        val interceptor = AuthHeaderInterceptor { "test-token" }
        val chain = CapturingChain("https://example.com/api/v1/auth/login")

        interceptor.intercept(chain)

        assertNull(chain.capturedRequest.header("Authorization"))
    }

    private class CapturingChain(url: String) : Interceptor.Chain {
        private val initialRequest = Request.Builder().url(url).get().build()
        lateinit var capturedRequest: Request

        override fun request(): Request = initialRequest

        override fun proceed(request: Request): Response {
            capturedRequest = request
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("{}".toResponseBody())
                .build()
        }

        override fun call() = throw UnsupportedOperationException()
        override fun connectTimeoutMillis(): Int = 0
        override fun connection() = null
        override fun readTimeoutMillis(): Int = 0
        override fun withConnectTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit): Interceptor.Chain = this
        override fun withReadTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit): Interceptor.Chain = this
        override fun withWriteTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit): Interceptor.Chain = this
        override fun writeTimeoutMillis(): Int = 0
    }
}
