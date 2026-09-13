package com.daily.nexamartpartner.features.auth.domain.model

data class RegistrationData(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val confirmPassword: String
)
