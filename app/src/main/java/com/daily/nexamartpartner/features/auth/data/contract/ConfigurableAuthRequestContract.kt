package com.daily.nexamartpartner.features.auth.data.contract

import com.daily.nexamartpartner.features.auth.domain.model.LoginCredentials

class ConfigurableAuthRequestContract(
    private val identifierField: String,
    private val passwordField: String,
    private val refreshTokenField: String
) : AuthRequestContract {
    override fun buildLoginBody(credentials: LoginCredentials): Map<String, String> {
        return mapOf(
            identifierField to credentials.identifier,
            passwordField to credentials.password
        )
    }

    override fun buildRefreshBody(refreshToken: String): Map<String, String> {
        return mapOf(refreshTokenField to refreshToken)
    }
}
