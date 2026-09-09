package com.daily.nexamartpartner.features.auth.data.source

import com.daily.nexamartpartner.core.network.ApiCallExecutor
import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.auth.data.contract.RegistrationRequestContract
import com.daily.nexamartpartner.features.auth.domain.model.RegistrationData

class RegistrationRemoteDataSourceImpl(
    private val api: RegistrationApi,
    private val requestContract: RegistrationRequestContract,
    private val apiCallExecutor: ApiCallExecutor
) : RegistrationRemoteDataSource {
    override suspend fun register(data: RegistrationData): AppResult<Unit> =
        requestContract.buildRegistrationRequest(data)?.let { request ->
            when (val result = apiCallExecutor.execute { api.register(request.endpoint, request.body) }) {
                is AppResult.Success -> AppResult.Success(Unit)
                is AppResult.Failure -> result
            }
        } ?: AppResult.Failure(
            AppFailure(
                message = "Account creation is not configured on the server yet. Please contact support.",
                type = FailureType.CONTRACT_MISSING
            )
        )
}
