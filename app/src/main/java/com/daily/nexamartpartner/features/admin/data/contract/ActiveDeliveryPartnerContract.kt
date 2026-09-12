package com.daily.nexamartpartner.features.admin.data.contract
import com.daily.nexamartpartner.features.admin.domain.model.*
class ActiveDeliveryPartnerContract : DeliveryPartnerContract {
 override val listPath="admin/delivery-partners";override val detailsPathTemplate="admin/delivery-partners/{id}";override val actionPathTemplate="admin/delivery-partners/{id}/action"
 override fun buildListQuery(q:DeliveryPartnersQuery)=buildMap{
  put("page",q.page.coerceAtLeast(0).toString());put("pageSize",q.pageSize.coerceIn(1,100).toString());
  q.searchText?.trim()?.takeIf{it.isNotBlank()}?.let{put("search",it)};
  q.filters.accountStatus?.let{put("status",it.backendValue)}
 }
 override fun buildActionBody(a:PartnerAdminAction,reason:String?)=buildMap{put("action",a.backendValue);reason?.let{put("reason",it)}}
 override fun resolvePath(t:String?,id:String)=t?.replace("{id}",id)
}
