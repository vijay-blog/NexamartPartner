package com.daily.nexamartpartner.features.auth.data.source

import com.daily.nexamartpartner.features.auth.data.model.LoginResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface RegistrationApi {
    @POST
    suspend fun register(@Url endpoint: String, @Body body: Map<String, String>): Response<LoginResponseDto>
}
