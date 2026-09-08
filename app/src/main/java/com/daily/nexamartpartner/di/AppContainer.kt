package com.daily.nexamartpartner.di

import android.content.Context
import com.daily.nexamartpartner.core.network.ApiCallExecutor
import com.daily.nexamartpartner.core.network.ApiClientFactory
import com.daily.nexamartpartner.core.network.AuthHeaderInterceptor
import com.daily.nexamartpartner.core.security.EncryptedSessionStorage
import com.daily.nexamartpartner.features.admin.data.contract.AdminDashboardContract
import com.daily.nexamartpartner.features.admin.data.contract.AdminOrdersContract
import com.daily.nexamartpartner.features.admin.data.contract.PendingBackendAdminDashboardContract
import com.daily.nexamartpartner.features.admin.data.contract.PendingBackendAdminOrdersContract
import com.daily.nexamartpartner.features.admin.data.repository.AdminDashboardRepositoryImpl
import com.daily.nexamartpartner.features.admin.data.repository.AdminOrdersRepositoryImpl
import com.daily.nexamartpartner.features.admin.data.source.AdminDashboardApi
import com.daily.nexamartpartner.features.admin.data.source.AdminDashboardRemoteDataSource
import com.daily.nexamartpartner.features.admin.data.source.AdminDashboardRemoteDataSourceImpl
import com.daily.nexamartpartner.features.admin.data.source.AdminOrdersApi
import com.daily.nexamartpartner.features.admin.data.source.AdminOrdersRemoteDataSource
import com.daily.nexamartpartner.features.admin.data.source.AdminOrdersRemoteDataSourceImpl
import com.daily.nexamartpartner.features.admin.domain.repository.AdminDashboardRepository
import com.daily.nexamartpartner.features.admin.domain.repository.AdminOrdersRepository
import com.daily.nexamartpartner.features.admin.domain.usecase.CancelAdminOrderUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.GetAdminDashboardUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.GetAdminOrderDetailsUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.GetAdminOrdersUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.UpdateAdminOrderStatusUseCase
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
    private val adminDashboardApi: AdminDashboardApi = retrofit.create(AdminDashboardApi::class.java)
    private val adminOrdersApi: AdminOrdersApi = retrofit.create(AdminOrdersApi::class.java)
    private val authRequestContract: AuthRequestContract = PendingBackendAuthRequestContract()
    private val adminDashboardContract: AdminDashboardContract = PendingBackendAdminDashboardContract()
    private val adminOrdersContract: AdminOrdersContract = PendingBackendAdminOrdersContract()
    private val apiCallExecutor = ApiCallExecutor()

    private val authRemoteDataSource: AuthRemoteDataSource = AuthRemoteDataSourceImpl(
        api = authApi,
        requestContract = authRequestContract,
        apiCallExecutor = apiCallExecutor
    )
    private val adminDashboardRemoteDataSource: AdminDashboardRemoteDataSource = AdminDashboardRemoteDataSourceImpl(
        api = adminDashboardApi,
        contract = adminDashboardContract,
        apiCallExecutor = apiCallExecutor
    )
    private val adminOrdersRemoteDataSource: AdminOrdersRemoteDataSource = AdminOrdersRemoteDataSourceImpl(
        api = adminOrdersApi,
        contract = adminOrdersContract,
        apiCallExecutor = apiCallExecutor
    )

    val authRepository: AuthRepository = AuthRepositoryImpl(
        remoteDataSource = authRemoteDataSource,
        sessionManager = sessionManager
    )
    private val adminDashboardRepository: AdminDashboardRepository = AdminDashboardRepositoryImpl(
        remoteDataSource = adminDashboardRemoteDataSource
    )
    private val adminOrdersRepository: AdminOrdersRepository = AdminOrdersRepositoryImpl(
        remoteDataSource = adminOrdersRemoteDataSource
    )

    var adminDashboardRepositoryOverride: AdminDashboardRepository? = null
    var adminOrdersRepositoryOverride: AdminOrdersRepository? = null

    val loginUseCase = LoginUseCase(authRepository)
    val restoreSessionUseCase = RestoreSessionUseCase(authRepository)
    val logoutUseCase = LogoutUseCase(authRepository)

    fun provideAdminDashboardUseCase(): GetAdminDashboardUseCase {
        return GetAdminDashboardUseCase(adminDashboardRepositoryOverride ?: adminDashboardRepository)
    }

    fun provideAdminOrdersUseCase(): GetAdminOrdersUseCase {
        return GetAdminOrdersUseCase(adminOrdersRepositoryOverride ?: adminOrdersRepository)
    }

    fun provideAdminOrderDetailsUseCase(): GetAdminOrderDetailsUseCase {
        return GetAdminOrderDetailsUseCase(adminOrdersRepositoryOverride ?: adminOrdersRepository)
    }

    fun provideUpdateAdminOrderStatusUseCase(): UpdateAdminOrderStatusUseCase {
        return UpdateAdminOrderStatusUseCase(adminOrdersRepositoryOverride ?: adminOrdersRepository)
    }

    fun provideCancelAdminOrderUseCase(): CancelAdminOrderUseCase {
        return CancelAdminOrderUseCase(adminOrdersRepositoryOverride ?: adminOrdersRepository)
    }
}
