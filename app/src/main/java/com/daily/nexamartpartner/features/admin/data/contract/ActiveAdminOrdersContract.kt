package com.daily.nexamartpartner.features.admin.data.contract

import com.daily.nexamartpartner.features.admin.domain.model.*

class ActiveAdminOrdersContract : AdminOrdersContract {
    override val listOrdersPath = "admin/orders"
    override val orderDetailsPathTemplate = "admin/orders/{id}"
    override val updateStatusPathTemplate = "admin/orders/{id}/status"
    override val cancelOrderPathTemplate = "admin/orders/{id}/cancel"
    override val assignDeliveryPathTemplate = "admin/orders/{id}/assign"

    override fun buildOrderListQuery(q: AdminOrdersQuery): Map<String, String> = buildMap {
        put("page", q.page.coerceAtLeast(0).toString())
        put("pageSize", q.pageSize.coerceIn(1, 100).toString())
        q.searchText?.trim()?.takeIf { it.isNotEmpty() }?.let { put("search", it) }
        q.filters.status?.let { put("status", it.backendValue) }
    }

    override fun buildUpdateStatusBody(status: OrderStatus): Map<String, String> =
        mapOf("status" to status.backendValue)

    override fun buildCancelOrderBody(reason: String?): Map<String, String> =
        mapOf("reason" to (reason?.trim()?.takeIf { it.isNotEmpty() } ?: "Cancelled by admin"))

    override fun buildAssignDeliveryBody(deliveryPartnerId: String): Map<String, String> =
        mapOf("deliveryPartnerId" to deliveryPartnerId.trim())

    override fun resolvePath(template: String?, orderId: String): String? =
        template?.replace("{id}", orderId.trim())
}
