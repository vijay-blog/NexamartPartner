package com.daily.nexamartpartner.features.delivery.data.model

import com.squareup.moshi.Json

data class DeliveryDashboardResponseDto(
    @field:Json(name = "activeOrders") val activeOrders: Long?,
    @field:Json(name = "assignedOrders") val assignedOrders: Long?,
    @field:Json(name = "pickedUpOrders") val pickedUpOrders: Long?,
    @field:Json(name = "outForDeliveryOrders") val outForDeliveryOrders: Long?,
    @field:Json(name = "completedToday") val completedToday: Long?,
    @field:Json(name = "todayEarnings") val todayEarnings: String?,
    @field:Json(name = "currencyCode") val currencyCode: String?,
    @field:Json(name = "availability") val availability: String?,
    @field:Json(name = "recentOrders") val recentOrders: List<DeliveryDashboardOrderSummaryDto>?
)

data class DeliveryDashboardOrderSummaryDto(
    @field:Json(name = "orderId") val orderId: String?,
    @field:Json(name = "status") val status: String?,
    @field:Json(name = "customerName") val customerName: String?,
    @field:Json(name = "createdAt") val createdAt: String?,
    @field:Json(name = "amount") val amount: String?
)
