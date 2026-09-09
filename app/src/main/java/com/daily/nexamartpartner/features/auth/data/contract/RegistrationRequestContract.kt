package com.daily.nexamartpartner.features.auth.data.contract

import com.daily.nexamartpartner.features.auth.domain.model.RegistrationData

data class RegistrationRequest(
    val endpoint: String,
    val body: Map<String, String>
)

interface RegistrationRequestContract {
    fun buildRegistrationRequest(data: RegistrationData): RegistrationRequest?
}

/**
 * Keeps registration field names and endpoint isolated until the Spring Boot
 * backend contract is confirmed. This prevents the Android app from guessing
 * a server API while still providing the complete registration flow.
 */
class PendingBackendRegistrationRequestContract : RegistrationRequestContract {
    override fun buildRegistrationRequest(data: RegistrationData): RegistrationRequest? = null
}

/** Use this once the backend confirms its registration endpoint/field names. */
class ConfigurableRegistrationRequestContract(
    private val endpoint: String,
    private val nameField: String,
    private val emailField: String,
    private val passwordField: String
) : RegistrationRequestContract {
    override fun buildRegistrationRequest(data: RegistrationData): RegistrationRequest {
        return RegistrationRequest(
            endpoint = endpoint,
            body = mapOf(
                nameField to data.name,
                emailField to data.email,
                passwordField to data.password
            )
        )
    }
}
