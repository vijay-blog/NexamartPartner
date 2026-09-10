package com.daily.nexamartpartner.features.delivery.data.contract
import com.daily.nexamartpartner.features.delivery.domain.model.*
class ActiveDeliveryOrderWorkflowContract:DeliveryOrderWorkflowContract{
 override val listAssignedOrdersPath="delivery/orders";override val listHistoryOrdersPath="delivery/orders";override val orderDetailsPathTemplate="delivery/orders/{id}";override val actionPathTemplate="delivery/orders/{id}/action"
 override fun buildListQuery(q:DeliveryOrdersQuery)=buildMap{put("page",q.page.toString());put("pageSize",q.pageSize.toString());q.searchText?.takeIf{it.isNotBlank()}?.let{put("searchText",it)};q.status?.let{put("status",it)};q.fromDate?.let{put("fromDate",it)};q.toDate?.let{put("toDate",it)}}
 override fun buildActionBody(a:DeliveryOrderAction)=mapOf("action" to a.backendValue)
 override fun resolvePath(t:String?,id:String)=t?.replace("{id}",id)
}
