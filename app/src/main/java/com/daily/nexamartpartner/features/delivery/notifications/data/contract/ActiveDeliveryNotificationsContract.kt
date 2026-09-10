package com.daily.nexamartpartner.features.delivery.notifications.data.contract
import com.daily.nexamartpartner.features.delivery.notifications.domain.model.DeliveryNotificationQuery
class ActiveDeliveryNotificationsContract:DeliveryNotificationsContract{
 override val listPath="delivery/notifications";override val markReadPath="delivery/notifications/{id}/read";override val markAllReadPath="delivery/notifications/read-all"
 override fun buildListQuery(q:DeliveryNotificationQuery)=mapOf("page" to q.page.toString(),"pageSize" to q.pageSize.toString(),"unreadOnly" to q.unreadOnly.toString())
}
