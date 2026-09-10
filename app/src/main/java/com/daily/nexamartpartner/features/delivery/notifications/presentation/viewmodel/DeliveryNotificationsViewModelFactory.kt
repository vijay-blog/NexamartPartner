package com.daily.nexamartpartner.features.delivery.notifications.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daily.nexamartpartner.features.delivery.notifications.domain.usecase.GetDeliveryNotificationsUseCase
import com.daily.nexamartpartner.features.delivery.notifications.domain.usecase.MarkAllDeliveryNotificationsReadUseCase
import com.daily.nexamartpartner.features.delivery.notifications.domain.usecase.MarkDeliveryNotificationReadUseCase

class DeliveryNotificationsViewModelFactory(
    private val get: GetDeliveryNotificationsUseCase,
    private val read: MarkDeliveryNotificationReadUseCase,
    private val readAll: MarkAllDeliveryNotificationsReadUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(DeliveryNotificationsViewModel::class.java)) {
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
        @Suppress("UNCHECKED_CAST")
        return DeliveryNotificationsViewModel(get, read, readAll) as T
    }
}
