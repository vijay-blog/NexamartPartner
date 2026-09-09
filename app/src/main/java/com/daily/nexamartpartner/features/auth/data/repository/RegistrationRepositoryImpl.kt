package com.daily.nexamartpartner.features.auth.data.repository

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.auth.data.source.RegistrationRemoteDataSource
import com.daily.nexamartpartner.features.auth.domain.model.RegistrationData
import com.daily.nexamartpartner.features.auth.domain.repository.RegistrationRepository

class RegistrationRepositoryImpl(
    private val remoteDataSource: RegistrationRemoteDataSource
) : RegistrationRepository {
    override suspend fun register(data: RegistrationData): AppResult<Unit> = remoteDataSource.register(data)
}
