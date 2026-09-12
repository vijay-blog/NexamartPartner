package com.daily.nexamartpartner.features.auth.data.contract

import com.daily.nexamartpartner.features.auth.domain.model.RegistrationData

class ActiveRegistrationRequestContract : RegistrationRequestContract {
    override fun buildRegistrationRequest(data: RegistrationData): RegistrationRequest = RegistrationRequest(
        endpoint = "auth/register",
        body = mapOf(
            "name" to data.name.trim(),
            "email" to data.email.trim().lowercase(),
            "password" to data.password,
            "confirmPassword" to data.confirmPassword
        )
    )
}
