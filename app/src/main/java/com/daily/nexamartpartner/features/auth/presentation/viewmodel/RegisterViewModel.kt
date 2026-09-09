package com.daily.nexamartpartner.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.auth.domain.model.RegistrationData
import com.daily.nexamartpartner.features.auth.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isSubmitting: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val formError: String? = null,
    val successMessage: String? = null
)

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChanged(value: String) = _uiState.update { it.copy(name = value, nameError = null, formError = null, successMessage = null) }
    fun onEmailChanged(value: String) = _uiState.update { it.copy(email = value, emailError = null, formError = null, successMessage = null) }
    fun onPasswordChanged(value: String) = _uiState.update { it.copy(password = value, passwordError = null, confirmPasswordError = null, formError = null, successMessage = null) }
    fun onConfirmPasswordChanged(value: String) = _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null, formError = null, successMessage = null) }

    fun submitRegistration() {
        val current = _uiState.value
        if (current.isSubmitting) return
        val nameError = if (current.name.trim().length < 2) "Please enter your name." else null
        val emailError = if (!EMAIL_REGEX.matches(current.email.trim())) "Please enter a valid email address." else null
        val passwordError = if (current.password.length < 8) "Password must be at least 8 characters." else null
        val confirmError = if (current.confirmPassword != current.password) "Passwords do not match." else null
        if (nameError != null || emailError != null || passwordError != null || confirmError != null) {
            _uiState.update { it.copy(nameError = nameError, emailError = emailError, passwordError = passwordError, confirmPasswordError = confirmError) }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, formError = null, successMessage = null) }
        viewModelScope.launch {
            when (val result = registerUseCase(RegistrationData(current.name.trim(), current.email.trim().lowercase(), current.password))) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isSubmitting = false, formError = null, successMessage = "Account created successfully. Please sign in with your email and password.") }
                }
                is AppResult.Failure -> {
                    _uiState.update { it.copy(isSubmitting = false, formError = result.error.message) }
                }
            }
        }
    }
}
