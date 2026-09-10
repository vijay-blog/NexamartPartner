package com.daily.nexamartpartner.features.admin.category.data.contract
import com.daily.nexamartpartner.features.admin.category.domain.model.*
class ActiveCategoryManagementContract : CategoryManagementContract {
 override val listPath="admin/categories";override val detailsPathTemplate="admin/categories/{id}";override val createPath="admin/categories";override val updatePathTemplate="admin/categories/{id}";override val actionPathTemplate="admin/categories/{id}/action"
 override fun buildListQuery(q:CategoryQuery)=buildMap{put("page",q.page.toString());put("pageSize",q.pageSize.toString());q.search?.takeIf{it.isNotBlank()}?.let{put("search",it)};q.active?.let{put("active",it.toString())}}
 override fun buildDraftBody(d:CategoryDraft)=buildMap{put("name",d.name);d.description?.let{put("description",it)};d.sortOrder?.let{put("sortOrder",it)}}
 override fun buildActionBody(a:CategoryAdminAction)=mapOf("action" to a.backendValue)
 override fun resolvePath(t:String?,id:String)=t?.replace("{id}",id)
}
