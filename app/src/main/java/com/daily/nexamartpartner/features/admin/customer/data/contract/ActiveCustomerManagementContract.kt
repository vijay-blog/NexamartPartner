package com.daily.nexamartpartner.features.admin.customer.data.contract
import com.daily.nexamartpartner.features.admin.customer.domain.model.*
class ActiveCustomerManagementContract : CustomerManagementContract {
 override val listPath="admin/customers";override val detailsPathTemplate="admin/customers/{id}";override val actionPathTemplate="admin/customers/{id}/action"
 override fun buildListQuery(q:CustomerQuery)=buildMap{put("page",q.page.toString());put("pageSize",q.pageSize.toString());q.search?.takeIf{it.isNotBlank()}?.let{put("search",it)};q.status?.let{put("status",it.backendValue)}}
 override fun buildActionBody(a:CustomerAdminAction)=mapOf("action" to a.backendValue)
 override fun resolvePath(t:String?,id:String)=t?.replace("{id}",id)
}
