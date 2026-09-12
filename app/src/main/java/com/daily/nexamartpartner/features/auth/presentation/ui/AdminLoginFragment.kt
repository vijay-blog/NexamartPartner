package com.daily.nexamartpartner.features.auth.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.di.appContainer
import com.daily.nexamartpartner.databinding.FragmentAdminLoginBinding
import com.daily.nexamartpartner.features.auth.domain.model.LoginCredentials
import com.daily.nexamartpartner.features.auth.domain.model.UserRole
import kotlinx.coroutines.launch

/** Production admin login. Credentials are validated by the backend; no hardcoded credentials or local fake tokens. */
class AdminLoginFragment : Fragment(R.layout.fragment_admin_login) {
    private var _binding: FragmentAdminLoginBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdminLoginBinding.bind(view)
        binding.adminSignInButton.setOnClickListener { signIn() }
        binding.adminBackButton.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
    }

    private fun signIn() {
        binding.adminErrorText.visibility = View.GONE
        val username = binding.adminUsernameInput.text?.toString()?.trim().orEmpty()
        val password = binding.adminPasswordInput.text?.toString().orEmpty()
        if (username.isBlank() || password.isBlank()) {
            binding.adminErrorText.text = getString(R.string.admin_credentials_required)
            binding.adminErrorText.visibility = View.VISIBLE
            return
        }

        binding.adminSignInButton.isEnabled = false
        lifecycleScope.launch {
            when (val result = requireContext().appContainer.loginUseCase(LoginCredentials(username, password))) {
                is AppResult.Success -> {
                    if (result.data.role == UserRole.ADMIN) {
                        requireContext().appContainer.authStateStore.setAuthenticated(result.data)
                    } else {
                        // loginUseCase persists the session; clear it before showing the role error.
                        requireContext().appContainer.logoutUseCase()
                        showError(getString(R.string.admin_access_required))
                    }
                }
                is AppResult.Failure -> showError(result.error.message)
            }
            if (_binding != null) binding.adminSignInButton.isEnabled = true
        }
    }

    private fun showError(message: String) {
        binding.adminErrorText.text = message.ifBlank { getString(R.string.admin_invalid_credentials) }
        binding.adminErrorText.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
