package com.daily.nexamartpartner.features.delivery.notifications.data.model

import com.squareup.moshi.Json

data class DeliveryNotificationDto(
    @field:Json(name = "id") val id: String?,
    @field:Json(name = "title") val title: String?,
    @field:Json(name = "message") val message: String?,
    @field:Json(name = "createdAt") val createdAt: String?,
    @field:Json(name = "read") val read: Boolean?,
    @field:Json(name = "type") val type: String?,
    @field:Json(name = "orderId") val orderId: Long?,
    @field:Json(name = "actionUrl") val actionUrl: String?
)

data class DeliveryNotificationPageDto(
    @field:Json(name = "content") val content: List<DeliveryNotificationDto>?,
    @field:Json(name = "page") val page: Int?,
    @field:Json(name = "pageSize") val pageSize: Int?,
    @field:Json(name = "totalPages") val totalPages: Int?,
    @field:Json(name = "totalElements") val totalElements: Long?,
    @field:Json(name = "hasNextPage") val hasNextPage: Boolean?,
    @field:Json(name = "number") val number: Int?,
    @field:Json(name = "size") val size: Int?,
    @field:Json(name = "unreadCount") val unreadCount: Int?
)
