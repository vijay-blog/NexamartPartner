package com.daily.nexamartpartner.features.delivery.availability.data.contract
import com.daily.nexamartpartner.features.delivery.availability.domain.model.DeliveryAvailabilityUpdate
class ActiveDeliveryAvailabilityContract:DeliveryAvailabilityContract{
 override val getPath="delivery/availability";override val updatePath="delivery/availability";override fun buildUpdateBody(u:DeliveryAvailabilityUpdate)=mapOf("available" to u.available)
}
