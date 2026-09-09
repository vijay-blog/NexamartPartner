package com.daily.nexamartpartner.features.auth.data.source

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.auth.domain.model.RegistrationData

interface RegistrationRemoteDataSource {
    suspend fun register(data: RegistrationData): AppResult<Unit>
}
