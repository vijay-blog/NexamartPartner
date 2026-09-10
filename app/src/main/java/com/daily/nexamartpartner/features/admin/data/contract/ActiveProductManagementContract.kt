package com.daily.nexamartpartner.features.admin.data.contract
import com.daily.nexamartpartner.features.admin.domain.model.*
class ActiveProductManagementContract : ProductManagementContract {
 override val listProductsPath="admin/products";override val productDetailsPathTemplate="admin/products/{id}";override val categoryOptionsPath="admin/products/category-options";override val createProductPath="admin/products";override val updateProductPathTemplate="admin/products/{id}";override val productActionPathTemplate="admin/products/{id}/action"
 override fun buildProductListQuery(q:ProductsQuery)=buildMap {put("page",q.page.toString());put("pageSize",q.pageSize.toString());q.searchText?.takeIf{it.isNotBlank()}?.let{put("searchText",it)};q.filters.categoryId?.let{put("categoryId",it)};q.filters.status?.let{put("status",it.backendValue)};put("sort",q.sort.backendValue)}
 override fun buildCreateProductBody(d:ProductDraft)=body(d)
 override fun buildUpdateProductBody(d:ProductDraft)=body(d)
 override fun buildProductActionBody(a:ProductAdminAction)=mapOf("action" to a.backendValue)
 override fun resolvePath(t:String?,id:String)=t?.replace("{id}",id)
 private fun body(d:ProductDraft)=buildMap {put("name",d.name);d.description?.let{put("description",it)};d.categoryId?.let{put("categoryId",it)};d.price?.let{put("price",it)};d.discountPercent?.let{put("discountPercent",it)};d.stock?.let{put("stock",it)};d.sku?.let{put("sku",it)};d.unit?.let{put("unit",it)}}
}
