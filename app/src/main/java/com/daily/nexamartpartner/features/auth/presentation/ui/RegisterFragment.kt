package com.daily.nexamartpartner.features.auth.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.databinding.FragmentRegisterBinding
import com.daily.nexamartpartner.di.appContainer
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.RegisterViewModel
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.RegisterViewModelFactory
import kotlinx.coroutines.launch

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(requireContext().appContainer.registerUseCase)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)
        binding.nameInputEditText.doAfterTextChanged { viewModel.onNameChanged(it?.toString().orEmpty()) }
        binding.emailInputEditText.doAfterTextChanged { viewModel.onEmailChanged(it?.toString().orEmpty()) }
        binding.passwordInputEditText.doAfterTextChanged { viewModel.onPasswordChanged(it?.toString().orEmpty()) }
        binding.confirmPasswordInputEditText.doAfterTextChanged { viewModel.onConfirmPasswordChanged(it?.toString().orEmpty()) }
        binding.registerButton.setOnClickListener { viewModel.submitRegistration() }
        binding.backToLoginButton.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.nameInputLayout.error = state.nameError
                    binding.emailInputLayout.error = state.emailError
                    binding.passwordInputLayout.error = state.passwordError
                    binding.confirmPasswordInputLayout.error = state.confirmPasswordError
                    binding.registerButton.isEnabled = !state.isSubmitting
                    binding.registerButton.text = if (state.isSubmitting) "Creating account…" else getString(R.string.create_account_button)
                    binding.registerErrorText.visibility = if (state.formError.isNullOrBlank() && state.successMessage.isNullOrBlank()) View.GONE else View.VISIBLE
                    binding.registerErrorText.text = state.formError ?: state.successMessage.orEmpty()
                    binding.registerErrorText.setTextColor(if (state.successMessage.isNullOrBlank()) requireContext().getColor(com.daily.nexamartpartner.R.color.nxm_error) else requireContext().getColor(com.daily.nexamartpartner.R.color.nxm_success))
                }
            }
        }
    }

    override fun onDestroyView() { _binding = null; super.onDestroyView() }
}
