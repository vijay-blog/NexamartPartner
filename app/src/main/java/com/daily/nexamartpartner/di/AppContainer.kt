package com.daily.nexamartpartner.di

import android.content.Context
import com.daily.nexamartpartner.core.network.ApiCallExecutor
import com.daily.nexamartpartner.core.network.ApiClientFactory
import com.daily.nexamartpartner.core.network.AuthHeaderInterceptor
import com.daily.nexamartpartner.core.security.EncryptedSessionStorage
import com.daily.nexamartpartner.features.auth.data.contract.AuthRequestContract
import com.daily.nexamartpartner.features.auth.data.contract.PendingBackendAuthRequestContract
import com.daily.nexamartpartner.features.auth.data.repository.AuthRepositoryImpl
import com.daily.nexamartpartner.features.auth.data.source.AuthApi
import com.daily.nexamartpartner.features.auth.data.source.AuthRemoteDataSource
import com.daily.nexamartpartner.features.auth.data.source.AuthRemoteDataSourceImpl
import com.daily.nexamartpartner.features.auth.domain.repository.AuthRepository
import com.daily.nexamartpartner.features.auth.domain.session.SessionManager
import com.daily.nexamartpartner.features.auth.domain.usecase.LoginUseCase
import com.daily.nexamartpartner.features.auth.domain.usecase.LogoutUseCase
import com.daily.nexamartpartner.features.auth.domain.usecase.RestoreSessionUseCase
import com.daily.nexamartpartner.features.auth.presentation.state.AuthStateStore
import retrofit2.Retrofit

class AppContainer(context: Context) {
    private val sessionStorage = EncryptedSessionStorage(context.applicationContext)
    val sessionManager = SessionManager(sessionStorage)
    val authStateStore = AuthStateStore()

    private val authHeaderInterceptor = AuthHeaderInterceptor {
        sessionManager.currentSession.value?.accessToken
    }

    private val retrofit: Retrofit = ApiClientFactory.create(authHeaderInterceptor)
    private val authApi: AuthApi = retrofit.create(AuthApi::class.java)
    private val authRequestContract: AuthRequestContract = PendingBackendAuthRequestContract()
    private val apiCallExecutor = ApiCallExecutor()

    private val authRemoteDataSource: AuthRemoteDataSource = AuthRemoteDataSourceImpl(
        api = authApi,
        requestContract = authRequestContract,
        apiCallExecutor = apiCallExecutor
    )

    val authRepository: AuthRepository = AuthRepositoryImpl(
        remoteDataSource = authRemoteDataSource,
        sessionManager = sessionManager
    )

    val loginUseCase = LoginUseCase(authRepository)
    val restoreSessionUseCase = RestoreSessionUseCase(authRepository)
    val logoutUseCase = LogoutUseCase(authRepository)
}
