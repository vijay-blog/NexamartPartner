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
import com.daily.nexamartpartner.features.admin.data.contract.DeliveryPartnerContract
import com.daily.nexamartpartner.features.admin.data.contract.PendingBackendDeliveryPartnerContract
import com.daily.nexamartpartner.features.admin.data.contract.PendingBackendProductManagementContract
import com.daily.nexamartpartner.features.admin.data.contract.ProductManagementContract
import com.daily.nexamartpartner.features.admin.data.repository.AdminDashboardRepositoryImpl
import com.daily.nexamartpartner.features.admin.data.repository.AdminOrdersRepositoryImpl
import com.daily.nexamartpartner.features.admin.data.repository.DeliveryPartnerRepositoryImpl
import com.daily.nexamartpartner.features.admin.data.repository.ProductManagementRepositoryImpl
import com.daily.nexamartpartner.features.admin.data.source.AdminDashboardApi
import com.daily.nexamartpartner.features.admin.data.source.AdminDashboardRemoteDataSource
import com.daily.nexamartpartner.features.admin.data.source.AdminDashboardRemoteDataSourceImpl
import com.daily.nexamartpartner.features.admin.data.source.AdminOrdersApi
import com.daily.nexamartpartner.features.admin.data.source.AdminOrdersRemoteDataSource
import com.daily.nexamartpartner.features.admin.data.source.AdminOrdersRemoteDataSourceImpl
import com.daily.nexamartpartner.features.admin.data.source.DeliveryPartnerApi
import com.daily.nexamartpartner.features.admin.data.source.DeliveryPartnerRemoteDataSource
import com.daily.nexamartpartner.features.admin.data.source.DeliveryPartnerRemoteDataSourceImpl
import com.daily.nexamartpartner.features.admin.data.source.ProductManagementApi
import com.daily.nexamartpartner.features.admin.data.source.ProductManagementRemoteDataSource
import com.daily.nexamartpartner.features.admin.data.source.ProductManagementRemoteDataSourceImpl
import com.daily.nexamartpartner.features.admin.domain.repository.AdminDashboardRepository
import com.daily.nexamartpartner.features.admin.domain.repository.AdminOrdersRepository
import com.daily.nexamartpartner.features.admin.domain.repository.DeliveryPartnerRepository
import com.daily.nexamartpartner.features.admin.domain.repository.ProductManagementRepository
import com.daily.nexamartpartner.features.admin.domain.usecase.CancelAdminOrderUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.CreateProductUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.GetAdminDashboardUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.GetAdminOrderDetailsUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.GetAdminOrdersUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.UpdateAdminOrderStatusUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.GetDeliveryPartnerDetailsUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.GetDeliveryPartnersUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.GetProductCategoryOptionsUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.GetProductDetailsUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.GetProductsUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.PerformProductAdminActionUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.UpdateDeliveryPartnerUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.UpdateProductUseCase
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
    private val deliveryPartnerApi: DeliveryPartnerApi = retrofit.create(DeliveryPartnerApi::class.java)
    private val productManagementApi: ProductManagementApi = retrofit.create(ProductManagementApi::class.java)
    private val authRequestContract: AuthRequestContract = PendingBackendAuthRequestContract()
    private val adminDashboardContract: AdminDashboardContract = PendingBackendAdminDashboardContract()
    private val adminOrdersContract: AdminOrdersContract = PendingBackendAdminOrdersContract()
    private val deliveryPartnerContract: DeliveryPartnerContract = PendingBackendDeliveryPartnerContract()
    private val productManagementContract: ProductManagementContract = PendingBackendProductManagementContract()
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
    private val deliveryPartnerRemoteDataSource: DeliveryPartnerRemoteDataSource =
        DeliveryPartnerRemoteDataSourceImpl(
            api = deliveryPartnerApi,
            contract = deliveryPartnerContract,
            executor = apiCallExecutor
        )
    private val productManagementRemoteDataSource: ProductManagementRemoteDataSource =
        ProductManagementRemoteDataSourceImpl(
            api = productManagementApi,
            contract = productManagementContract,
            executor = apiCallExecutor
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
    private val deliveryPartnerRepository: DeliveryPartnerRepository =
        DeliveryPartnerRepositoryImpl(deliveryPartnerRemoteDataSource)
    private val productManagementRepository: ProductManagementRepository =
        ProductManagementRepositoryImpl(productManagementRemoteDataSource)

    var adminDashboardRepositoryOverride: AdminDashboardRepository? = null
    var adminOrdersRepositoryOverride: AdminOrdersRepository? = null
    var deliveryPartnerRepositoryOverride: DeliveryPartnerRepository? = null
    var productManagementRepositoryOverride: ProductManagementRepository? = null

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

    fun provideDeliveryPartnersUseCase(): GetDeliveryPartnersUseCase =
        GetDeliveryPartnersUseCase(deliveryPartnerRepositoryOverride ?: deliveryPartnerRepository)

    fun provideDeliveryPartnerDetailsUseCase(): GetDeliveryPartnerDetailsUseCase =
        GetDeliveryPartnerDetailsUseCase(deliveryPartnerRepositoryOverride ?: deliveryPartnerRepository)

    fun provideUpdateDeliveryPartnerUseCase(): UpdateDeliveryPartnerUseCase =
        UpdateDeliveryPartnerUseCase(deliveryPartnerRepositoryOverride ?: deliveryPartnerRepository)

    fun provideGetProductsUseCase(): GetProductsUseCase =
        GetProductsUseCase(productManagementRepositoryOverride ?: productManagementRepository)

    fun provideGetProductDetailsUseCase(): GetProductDetailsUseCase =
        GetProductDetailsUseCase(productManagementRepositoryOverride ?: productManagementRepository)

    fun provideGetProductCategoryOptionsUseCase(): GetProductCategoryOptionsUseCase =
        GetProductCategoryOptionsUseCase(productManagementRepositoryOverride ?: productManagementRepository)

    fun provideCreateProductUseCase(): CreateProductUseCase =
        CreateProductUseCase(productManagementRepositoryOverride ?: productManagementRepository)

    fun provideUpdateProductUseCase(): UpdateProductUseCase =
        UpdateProductUseCase(productManagementRepositoryOverride ?: productManagementRepository)

    fun providePerformProductAdminActionUseCase(): PerformProductAdminActionUseCase =
        PerformProductAdminActionUseCase(productManagementRepositoryOverride ?: productManagementRepository)
}
