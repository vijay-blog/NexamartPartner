package com.daily.nexamartpartner.core.network

import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import java.io.IOException
import retrofit2.Response

class ApiCallExecutor {
    suspend fun <T> execute(call: suspend () -> Response<T>): AppResult<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    AppResult.Success(body)
                } else {
                    AppResult.Failure(
                        AppFailure(
                            message = "Empty response from server.",
                            code = response.code(),
                            type = FailureType.SERVER
                        )
                    )
                }
            } else {
                val type = when (response.code()) {
                    401 -> FailureType.UNAUTHORIZED
                    403 -> FailureType.FORBIDDEN
                    in 500..599 -> FailureType.SERVER
                    else -> FailureType.UNKNOWN
                }
                AppResult.Failure(
                    AppFailure(
                        message = ErrorMessageResolver.resolve(response.code()),
                        code = response.code(),
                        type = type
                    )
                )
            }
        } catch (_: IOException) {
            AppResult.Failure(
                AppFailure(
                    message = "Unable to connect to the server. Please check your internet connection.",
                    type = FailureType.NETWORK
                )
            )
        } catch (throwable: Throwable) {
            AppResult.Failure(
                AppFailure(
                    message = throwable.message ?: "Unexpected error.",
                    type = FailureType.UNKNOWN
                )
            )
        }
    }
}
