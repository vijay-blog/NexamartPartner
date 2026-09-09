package com.daily.nexamartpartner.features.auth.domain.repository

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.auth.domain.model.RegistrationData

interface RegistrationRepository {
    suspend fun register(data: RegistrationData): AppResult<Unit>
}
