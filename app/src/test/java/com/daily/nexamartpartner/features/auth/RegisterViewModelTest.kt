package com.daily.nexamartpartner.features.auth

import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.auth.domain.model.RegistrationData
import com.daily.nexamartpartner.features.auth.domain.usecase.RegisterUseCase
import com.daily.nexamartpartner.features.auth.domain.repository.RegistrationRepository
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.RegisterViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class RegisterViewModelTest {
    @Test
    fun invalidEmailAndShortPassword_areRejectedBeforeUseCase() = runTest {
        val vm = RegisterViewModel(RegisterUseCase(FakeRepository()))
        vm.onNameChanged("Test User")
        vm.onEmailChanged("bad-email")
        vm.onPasswordChanged("123")
        vm.onConfirmPasswordChanged("123")
        vm.submitRegistration()
        val state = vm.uiState.value
        assertEquals("Please enter a valid email address.", state.emailError)
        assertEquals("Password must be at least 8 characters.", state.passwordError)
        assertFalse(state.isSubmitting)
    }

    @Test
    fun mismatchedPasswords_areRejected() = runTest {
        val vm = RegisterViewModel(RegisterUseCase(FakeRepository()))
        vm.onNameChanged("Test User")
        vm.onEmailChanged("test@example.com")
        vm.onPasswordChanged("password123")
        vm.onConfirmPasswordChanged("password124")
        vm.submitRegistration()
        assertEquals("Passwords do not match.", vm.uiState.value.confirmPasswordError)
    }

    private class FakeRepository : RegistrationRepository {
        override suspend fun register(data: RegistrationData): AppResult<Unit> =
            AppResult.Failure(AppFailure("not configured", type = FailureType.CONTRACT_MISSING))
    }
}
