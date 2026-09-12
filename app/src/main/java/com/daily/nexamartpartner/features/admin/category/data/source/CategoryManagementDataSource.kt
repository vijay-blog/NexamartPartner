package com.daily.nexamartpartner.features.admin.category.data.source

import com.daily.nexamartpartner.core.network.ApiCallExecutor
import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.admin.category.data.contract.CategoryManagementContract
import com.daily.nexamartpartner.features.admin.category.data.model.CategoriesPageDto
import com.daily.nexamartpartner.features.admin.category.data.model.CategoryDto
import com.daily.nexamartpartner.features.admin.category.domain.model.CategoryAdminAction
import com.daily.nexamartpartner.features.admin.category.domain.model.CategoryDraft
import com.daily.nexamartpartner.features.admin.category.domain.model.CategoryQuery
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface CategoryManagementApi {
    @GET
    suspend fun list(
        @Url path: String,
        @QueryMap params: Map<String, String>
    ): Response<CategoriesPageDto>

    @GET
    suspend fun details(@Url path: String): Response<CategoryDto>

    @POST
    suspend fun create(
        @Url path: String,
        @Body body: Map<String, String>
    ): Response<CategoryDto>

    @PUT
    suspend fun update(
        @Url path: String,
        @Body body: Map<String, String>
    ): Response<CategoryDto>

    @PATCH
    suspend fun action(
        @Url path: String,
        @Body body: Map<String, String>
    ): Response<Unit>
}

interface CategoryManagementRemoteDataSource {
    suspend fun list(q: CategoryQuery): AppResult<CategoriesPageDto>
    suspend fun details(id: String): AppResult<CategoryDto>
    suspend fun create(d: CategoryDraft): AppResult<CategoryDto>
    suspend fun update(id: String, d: CategoryDraft): AppResult<CategoryDto>
    suspend fun action(id: String, a: CategoryAdminAction): AppResult<Unit>
}

class CategoryManagementRemoteDataSourceImpl(
    private val api: CategoryManagementApi,
    private val contract: CategoryManagementContract,
    private val executor: ApiCallExecutor
) : CategoryManagementRemoteDataSource {
    private fun <T> missing(message: String): AppResult<T> =
        AppResult.Failure(AppFailure(message, type = FailureType.CONTRACT_MISSING))

    override suspend fun list(q: CategoryQuery): AppResult<CategoriesPageDto> {
        val path = contract.listPath
            ?: return missing("Category list API contract is not confirmed yet.")
        val params = contract.buildListQuery(q)
            ?: return missing("Category list query contract is not confirmed yet.")
        return executor.execute { api.list(path, params) }
    }

    override suspend fun details(id: String): AppResult<CategoryDto> {
        val path = contract.resolvePath(contract.detailsPathTemplate, id)
            ?: return missing("Category details API contract is not confirmed yet.")
        return executor.execute { api.details(path) }
    }

    override suspend fun create(d: CategoryDraft): AppResult<CategoryDto> {
        val path = contract.createPath
            ?: return missing("Category creation API contract is not confirmed yet.")
        val body = contract.buildDraftBody(d)
            ?: return missing("Category creation request contract is not confirmed yet.")
        return executor.execute { api.create(path, body) }
    }

    override suspend fun update(id: String, d: CategoryDraft): AppResult<CategoryDto> {
        val path = contract.resolvePath(contract.updatePathTemplate, id)
            ?: return missing("Category update API contract is not confirmed yet.")
        val body = contract.buildDraftBody(d)
            ?: return missing("Category update request contract is not confirmed yet.")
        return executor.execute { api.update(path, body) }
    }

    override suspend fun action(id: String, a: CategoryAdminAction): AppResult<Unit> {
        val path = contract.resolvePath(contract.actionPathTemplate, id)
            ?: return missing("Category action API contract is not confirmed yet.")
        val body = contract.buildActionBody(a)
            ?: return missing("Category action request contract is not confirmed yet.")

        return when (val result = executor.execute { api.action(path, body) }) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> result
        }
    }
}
