package com.daily.nexamartpartner.features.auth.data.contract

import com.daily.nexamartpartner.features.auth.domain.model.LoginCredentials

/** Production NexaMart Spring Boot authentication contract. */
class ActiveAuthRequestContract : AuthRequestContract {
    override fun buildLoginBody(credentials: LoginCredentials): Map<String, String> = mapOf(
        "email" to credentials.identifier.trim().lowercase(),
        "password" to credentials.password
    )
    override fun buildRefreshBody(refreshToken: String): Map<String, String> = mapOf("refreshToken" to refreshToken)
}
