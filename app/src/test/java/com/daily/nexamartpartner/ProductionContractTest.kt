package com.daily.nexamartpartner

import com.daily.nexamartpartner.core.network.ApiCallExecutor
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.admin.data.contract.ActiveProductManagementContract
import com.daily.nexamartpartner.features.admin.domain.model.ProductAdminAction
import com.daily.nexamartpartner.features.admin.domain.model.ProductDraft
import com.daily.nexamartpartner.features.admin.domain.model.ProductFilters
import com.daily.nexamartpartner.features.admin.domain.model.ProductSort
import com.daily.nexamartpartner.features.admin.domain.model.ProductsQuery
import com.daily.nexamartpartner.features.auth.data.contract.ActiveAuthRequestContract
import com.daily.nexamartpartner.features.auth.data.contract.ActiveRegistrationRequestContract
import com.daily.nexamartpartner.features.auth.domain.model.LoginCredentials
import com.daily.nexamartpartner.features.auth.domain.model.RegistrationData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class ProductionContractTest {
    @Test
    fun loginUsesBackendIdentifierField() {
        val body = ActiveAuthRequestContract().buildLoginBody(LoginCredentials("admin", "secret"))
        assertEquals("admin", body["identifier"])
        assertEquals("secret", body["password"])
        assertTrue("email" !in body)
    }

    @Test
    fun registrationIncludesConfirmPassword() {
        val request = ActiveRegistrationRequestContract().buildRegistrationRequest(
            RegistrationData("Partner", "partner@example.com", "password123", "password123")
        )
        assertEquals("auth/register", request.endpoint)
        assertEquals("password123", request.body["confirmPassword"])
    }

    @Test
    fun productContractMatchesAdminBackend() {
        val contract = ActiveProductManagementContract()
        val query = contract.buildProductListQuery(
            ProductsQuery(0, 20, "rice", ProductFilters(), ProductSort.NEWEST)
        )
        assertEquals("admin/products", contract.listProductsPath)
        assertEquals("admin/products/categories", contract.categoryOptionsPath)
        assertEquals("rice", query["search"])
        assertEquals("NEWEST", query["sort"])
        assertEquals("20", query["pageSize"])
    }

    @Test
    fun productDeleteActionUsesBackendActionBody() {
        val contract = ActiveProductManagementContract()
        assertEquals(mapOf("action" to "DELETE"), contract.buildProductActionBody(ProductAdminAction.DELETE))
        assertEquals("admin/products/42", contract.resolvePath(contract.productDetailsPathTemplate, "42"))
    }

    @Test
    fun noContentResponseIsSuccessfulForUnitActions() = runTest {
        val result = ApiCallExecutor().execute { Response.success(204, null as Unit?) }
        assertTrue(result is AppResult.Success)
    }
}
