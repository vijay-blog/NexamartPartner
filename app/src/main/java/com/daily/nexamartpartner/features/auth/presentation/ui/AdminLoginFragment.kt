package com.daily.nexamartpartner.features.auth.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.databinding.FragmentAdminLoginBinding
import com.daily.nexamartpartner.di.appContainer
import com.daily.nexamartpartner.features.auth.domain.model.UserRole
import com.daily.nexamartpartner.features.auth.domain.model.UserSession
import kotlinx.coroutines.launch

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
        if (username != ADMIN_USERNAME || password != ADMIN_PASSWORD) {
            binding.adminErrorText.text = getString(R.string.admin_invalid_credentials)
            binding.adminErrorText.visibility = View.VISIBLE
            return
        }
        val session = UserSession(
            accessToken = LOCAL_ADMIN_ACCESS_TOKEN,
            refreshToken = LOCAL_ADMIN_REFRESH_TOKEN,
            userId = ADMIN_USER_ID,
            name = "Admin",
            contact = ADMIN_USERNAME,
            role = UserRole.ADMIN
        )
        binding.adminSignInButton.isEnabled = false
        lifecycleScope.launch {
            requireContext().appContainer.sessionManager.saveSession(session)
            requireContext().appContainer.authStateStore.setAuthenticated(session)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        // Requested bootstrap credentials. Replace with server authentication before production release.
        const val ADMIN_USERNAME = "admin"
        const val ADMIN_PASSWORD = "admin@223"
        private const val ADMIN_USER_ID = 1L
        private const val LOCAL_ADMIN_ACCESS_TOKEN = "local-admin-session"
        private const val LOCAL_ADMIN_REFRESH_TOKEN = "local-admin-refresh"
    }
}
