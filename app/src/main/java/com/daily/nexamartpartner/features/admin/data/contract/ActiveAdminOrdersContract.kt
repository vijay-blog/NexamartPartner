package com.daily.nexamartpartner.features.admin.data.contract
import com.daily.nexamartpartner.features.admin.domain.model.*
class ActiveAdminOrdersContract : AdminOrdersContract {
 override val listOrdersPath="admin/orders"; override val orderDetailsPathTemplate="admin/orders/{id}"; override val updateStatusPathTemplate="admin/orders/{id}/status"; override val cancelOrderPathTemplate="admin/orders/{id}/cancel"; override val assignDeliveryPathTemplate=null
 override fun buildOrderListQuery(q:AdminOrdersQuery)=buildMap { put("page",q.page.toString());put("pageSize",q.pageSize.toString());q.searchText?.takeIf{it.isNotBlank()}?.let{put("searchText",it)};q.filters.status?.let{put("status",mapStatus(it))};q.filters.paymentStatus?.let{put("paymentStatus",it.backendValue)};put("sort",q.sort.backendValue) }
 override fun buildUpdateStatusBody(status:OrderStatus)=mapOf("status" to mapStatus(status))
 override fun buildCancelOrderBody(reason:String?)=mapOf("reason" to (reason ?: "Cancelled by admin"))
 override fun resolvePath(template:String?,orderId:String)=template?.replace("{id}",orderId)
 private fun mapStatus(s:OrderStatus)=when(s){OrderStatus.PENDING->"CREATED";OrderStatus.CONFIRMED->"PARTNER_SEARCHING";OrderStatus.PREPARING->"PICKING";OrderStatus.READY->"PACKED";OrderStatus.ASSIGNED->"DELIVERY_ASSIGNED";OrderStatus.ACCEPTED->"PARTNER_ACCEPTED";else->s.backendValue}
}
