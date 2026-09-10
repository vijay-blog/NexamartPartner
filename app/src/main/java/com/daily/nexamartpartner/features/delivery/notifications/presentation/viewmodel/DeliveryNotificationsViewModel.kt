package com.daily.nexamartpartner.features.delivery.notifications.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.delivery.notifications.domain.model.DeliveryNotificationQuery
import com.daily.nexamartpartner.features.delivery.notifications.presentation.state.DeliveryNotificationsUiState
import com.daily.nexamartpartner.features.delivery.notifications.presentation.state.NotificationContent
import com.daily.nexamartpartner.features.delivery.notifications.domain.usecase.GetDeliveryNotificationsUseCase
import com.daily.nexamartpartner.features.delivery.notifications.domain.usecase.MarkAllDeliveryNotificationsReadUseCase
import com.daily.nexamartpartner.features.delivery.notifications.domain.usecase.MarkDeliveryNotificationReadUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeliveryNotificationsViewModel(
    private val get: GetDeliveryNotificationsUseCase,
    private val read: MarkDeliveryNotificationReadUseCase,
    private val readAll: MarkAllDeliveryNotificationsReadUseCase
) : ViewModel() {
    sealed interface Event {
        data object SessionExpired : Event
    }

    private val _state = MutableStateFlow(DeliveryNotificationsUiState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    private val items = mutableListOf<com.daily.nexamartpartner.features.delivery.notifications.domain.model.DeliveryNotification>()
    private var page = 0
    private var next = true
    private var busy = false

    init {
        load(false)
    }

    fun refresh() {
        if (busy) return
        _state.update { it.copy(isRefreshing = true) }
        load(false)
    }

    fun retry() = load(true)

    fun nextPage() {
        if (busy || !next) return
        _state.update { it.copy(isLoadingMore = true) }
        fetch(page + 1, append = true)
    }

    fun markRead(id: String) {
        viewModelScope.launch {
            when (val result = read(id)) {
                is AppResult.Success -> {
                    val index = items.indexOfFirst { it.id == id }
                    if (index >= 0) {
                        items[index] = items[index].copy(read = true)
                        _state.update {
                            it.copy(
                                unreadCount = items.count { item -> !item.read },
                                content = NotificationContent.Success(items.toList(), next)
                            )
                        }
                    }
                }

                is AppResult.Failure -> if (result.error.type == FailureType.UNAUTHORIZED) {
                    _events.tryEmit(Event.SessionExpired)
                }
            }
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            when (val result = readAll()) {
                is AppResult.Success -> {
                    items.indices.forEach { index ->
                        items[index] = items[index].copy(read = true)
                    }
                    _state.update {
                        it.copy(unreadCount = 0, content = NotificationContent.Success(items.toList(), next))
                    }
                }

                is AppResult.Failure -> if (result.error.type == FailureType.UNAUTHORIZED) {
                    _events.tryEmit(Event.SessionExpired)
                }
            }
        }
    }

    private fun load(force: Boolean) {
        if (force) _state.update { it.copy(content = NotificationContent.Loading) }
        fetch(0, append = false)
    }

    private fun fetch(pageValue: Int, append: Boolean) {
        if (busy) return
        busy = true
        viewModelScope.launch {
            when (val result = get(DeliveryNotificationQuery(pageValue, 20))) {
                is AppResult.Success -> {
                    if (!append) items.clear()
                    items.addAll(result.data.items)
                    page = result.data.page
                    next = result.data.hasNextPage
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            isLoadingMore = false,
                            unreadCount = result.data.unreadCount ?: items.count { item -> !item.read },
                            content = if (items.isEmpty()) {
                                NotificationContent.Empty("You're all caught up", "New delivery updates will appear here.")
                            } else {
                                NotificationContent.Success(items.toList(), next)
                            }
                        )
                    }
                }

                is AppResult.Failure -> {
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            isLoadingMore = false,
                            content = if (result.error.type == FailureType.CONTRACT_MISSING) {
                                NotificationContent.Unavailable("Notifications unavailable", result.error.message)
                            } else {
                                NotificationContent.Error(
                                    if (result.error.type == FailureType.UNAUTHORIZED) "Session expired" else "Unable to load notifications",
                                    result.error.message
                                )
                            }
                        )
                    }
                    if (result.error.type == FailureType.UNAUTHORIZED) {
                        _events.tryEmit(Event.SessionExpired)
                    }
                }
            }
            busy = false
        }
    }
}
