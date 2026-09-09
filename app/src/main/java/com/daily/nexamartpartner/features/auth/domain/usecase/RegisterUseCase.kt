package com.daily.nexamartpartner.features.auth.domain.usecase

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.auth.domain.model.RegistrationData
import com.daily.nexamartpartner.features.auth.domain.repository.RegistrationRepository

class RegisterUseCase(private val repository: RegistrationRepository) {
    suspend operator fun invoke(data: RegistrationData): AppResult<Unit> = repository.register(data)
}
