package com.daily.nexamartpartner.features.admin.data.contract

import com.daily.nexamartpartner.features.admin.domain.model.*

/** Exact contract for the Spring Boot partner backend in this repository. */
class ActiveProductManagementContract : ProductManagementContract {
    override val listProductsPath = "admin/products"
    override val productDetailsPathTemplate = "admin/products/{id}"
    override val categoryOptionsPath = "admin/products/categories"
    override val createProductPath = "admin/products"
    override val updateProductPathTemplate = "admin/products/{id}"
    override val productActionPathTemplate = "admin/products/{id}/action"

    override fun buildProductListQuery(query: ProductsQuery): Map<String, String> = buildMap {
        put("page", query.page.coerceAtLeast(0).toString())
        put("pageSize", query.pageSize.coerceIn(1, 100).toString())
        query.searchText?.trim()?.takeIf { it.isNotEmpty() }?.let { put("search", it) }
        query.filters.categoryId?.trim()?.takeIf { it.isNotEmpty() }?.let { put("categoryId", it) }
        when (query.filters.status) {
            ProductStatus.ACTIVE -> put("active", "true")
            ProductStatus.INACTIVE -> put("active", "false")
            ProductStatus.OUT_OF_STOCK -> put("outOfStock", "true")
            ProductStatus.DRAFT, ProductStatus.UNKNOWN, null -> Unit
        }
        put("sort", query.sort.backendValue)
    }

    override fun buildCreateProductBody(draft: ProductDraft): Map<String, String> = body(draft)
    override fun buildUpdateProductBody(draft: ProductDraft): Map<String, String> = body(draft)
    override fun buildProductActionBody(action: ProductAdminAction): Map<String, String> =
        mapOf("action" to action.backendValue)

    override fun resolvePath(template: String?, productId: String): String? =
        template?.replace("{id}", productId.trim())

    private fun body(d: ProductDraft): Map<String, String> = buildMap {
        put("name", d.name.trim())
        d.description?.trim()?.takeIf { it.isNotEmpty() }?.let { put("description", it) }
        d.categoryId?.trim()?.takeIf { it.isNotEmpty() }?.let { put("categoryId", it) }
        d.price?.trim()?.takeIf { it.isNotEmpty() }?.let { put("price", it) }
        d.discountPercent?.trim()?.takeIf { it.isNotEmpty() }?.let { put("discountPercent", it) }
        d.stock?.trim()?.takeIf { it.isNotEmpty() }?.let { put("stock", it) }
        d.sku?.trim()?.takeIf { it.isNotEmpty() }?.let { put("sku", it) }
        d.unit?.trim()?.takeIf { it.isNotEmpty() }?.let { put("unit", it) }
    }
}
