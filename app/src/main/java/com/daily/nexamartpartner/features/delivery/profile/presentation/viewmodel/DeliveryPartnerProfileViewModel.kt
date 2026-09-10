package com.daily.nexamartpartner.features.delivery.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.delivery.profile.domain.model.DeliveryPartnerProfileUpdate
import com.daily.nexamartpartner.features.delivery.profile.domain.usecase.GetDeliveryPartnerProfileUseCase
import com.daily.nexamartpartner.features.delivery.profile.domain.usecase.UpdateDeliveryPartnerProfileUseCase
import com.daily.nexamartpartner.features.delivery.profile.presentation.state.DeliveryPartnerProfileUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeliveryPartnerProfileViewModel(
    private val get: GetDeliveryPartnerProfileUseCase,
    private val update: UpdateDeliveryPartnerProfileUseCase
) : ViewModel() {
    sealed interface Event {
        data object SessionExpired : Event
        data class Message(val text: String) : Event
    }

    private val _state = MutableStateFlow(DeliveryPartnerProfileUiState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, unavailable = false) }
            when (val result = get()) {
                is AppResult.Success -> {
                    _state.value = DeliveryPartnerProfileUiState(loading = false, profile = result.data)
                }

                is AppResult.Failure -> handle(result)
            }
        }
    }

    fun save(updateRequest: DeliveryPartnerProfileUpdate) {
        viewModelScope.launch {
            _state.update { it.copy(saving = true, error = null) }
            when (val result = update(updateRequest)) {
                is AppResult.Success -> {
                    _state.value = DeliveryPartnerProfileUiState(profile = result.data)
                    _events.emit(Event.Message("Profile updated successfully."))
                }

                is AppResult.Failure -> handle(result, saving = false)
            }
        }
    }

    private suspend fun handle(result: AppResult.Failure, saving: Boolean = false) {
        when (result.error.type) {
            FailureType.UNAUTHORIZED -> {
                _events.emit(Event.SessionExpired)
                _state.update { it.copy(loading = false, saving = saving) }
            }

            FailureType.CONTRACT_MISSING -> {
                _state.update { it.copy(loading = false, saving = saving, unavailable = true) }
            }

            else -> {
                _state.update { it.copy(loading = false, saving = saving, error = result.error.message) }
            }
        }
    }
}
