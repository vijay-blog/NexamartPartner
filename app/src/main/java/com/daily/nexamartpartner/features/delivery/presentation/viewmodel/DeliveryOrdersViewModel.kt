package com.daily.nexamartpartner.features.delivery.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.delivery.domain.model.DeliveryOrderSummary
import com.daily.nexamartpartner.features.delivery.domain.model.DeliveryOrdersQuery
import com.daily.nexamartpartner.features.delivery.domain.usecase.GetAssignedDeliveryOrdersUseCase
import com.daily.nexamartpartner.features.delivery.presentation.state.DeliveryOrdersUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeliveryOrdersViewModel(
    private val getOrders: GetAssignedDeliveryOrdersUseCase
) : ViewModel() {
    sealed interface Event {
        data object SessionExpired : Event
    }

    private val _state = MutableStateFlow(DeliveryOrdersUiState())
    val state: StateFlow<DeliveryOrdersUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    private val loaded = mutableListOf<DeliveryOrderSummary>()
    private var page = 0
    private var next = true
    private var job: Job? = null
    private var debounce: Job? = null

    init {
        load(force = false)
    }

    fun search(value: String) {
        _state.update { it.copy(search = value) }
        debounce?.cancel()
        debounce = viewModelScope.launch {
            delay(350)
            load(force = true)
        }
    }

    fun refresh() {
        if (job?.isActive == true) return
        _state.update { it.copy(isRefreshing = true) }
        load(force = false)
    }

    fun retry() = load(force = true)

    fun nextPage() {
        if (!next || job?.isActive == true) return
        _state.update { it.copy(isLoadingMore = true) }
        fetch(page + 1, append = true)
    }

    private fun load(force: Boolean) {
        fetch(0, append = false, force = force)
    }

    private fun fetch(p: Int, append: Boolean = false, force: Boolean = false) {
        if (job?.isActive == true) return
        if (force) _state.update { it.copy(content = DeliveryOrdersUiState.Content.Loading) }

        job = viewModelScope.launch {
            when (val result = getOrders(DeliveryOrdersQuery(p, 20, _state.value.search.trim().ifBlank { null }))) {
                is AppResult.Success -> {
                    if (!append) loaded.clear()
                    loaded.addAll(result.data.orders)
                    page = result.data.page
                    next = result.data.hasNextPage
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            isLoadingMore = false,
                            content = if (loaded.isEmpty()) {
                                DeliveryOrdersUiState.Content.Empty(
                                    "No assigned orders",
                                    "There are no delivery orders assigned to you right now."
                                )
                            } else {
                                DeliveryOrdersUiState.Content.Success(loaded.toList(), next)
                            }
                        )
                    }
                }

                is AppResult.Failure -> {
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            isLoadingMore = false,
                            content = when (result.error.type) {
                                FailureType.CONTRACT_MISSING -> DeliveryOrdersUiState.Content.Unavailable(
                                    "Assigned orders unavailable",
                                    result.error.message
                                )

                                FailureType.UNAUTHORIZED -> DeliveryOrdersUiState.Content.Error(
                                    "Session expired",
                                    "Please sign in again."
                                )

                                else -> DeliveryOrdersUiState.Content.Error(
                                    "Unable to load orders",
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
        }
    }
}
