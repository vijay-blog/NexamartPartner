package com.daily.nexamartpartner.features.auth.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.databinding.FragmentCreateDeliveryAccountBinding
import com.daily.nexamartpartner.di.appContainer
import com.daily.nexamartpartner.features.auth.domain.model.RegistrationData
import kotlinx.coroutines.launch

/** Production delivery-partner registration backed by the Spring Boot registration API. */
class CreateDeliveryAccountFragment : Fragment(R.layout.fragment_create_delivery_account) {
    private var _binding: FragmentCreateDeliveryAccountBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateDeliveryAccountBinding.bind(view)
        binding.createAccountButton.setOnClickListener { createAccount() }
        binding.createAccountBackButton.setOnClickListener { findNavController().navigateUp() }
    }

    private fun createAccount() {
        binding.createAccountErrorText.visibility = View.GONE
        val name = binding.createNameInput.text?.toString()?.trim().orEmpty()
        val email = binding.createEmailInput.text?.toString()?.trim().orEmpty()
        val password = binding.createPasswordInput.text?.toString().orEmpty()
        val confirm = binding.createConfirmPasswordInput.text?.toString().orEmpty()

        when {
            name.length < 2 -> showError(getString(R.string.create_account_name_error))
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> showError(getString(R.string.create_account_email_error))
            password.length < 8 -> showError(getString(R.string.create_account_password_error))
            password != confirm -> showError(getString(R.string.create_account_confirm_error))
            else -> submit(name, email, password, confirm)
        }
    }

    private fun submit(name: String, email: String, password: String, confirm: String) {
        binding.createAccountButton.isEnabled = false
        binding.createAccountButton.text = getString(R.string.create_account_creating)
        viewLifecycleOwner.lifecycleScope.launch {
            when (val result = requireContext().appContainer.registerUseCase(
                RegistrationData(name, email.lowercase(), password, confirm)
            )) {
                is AppResult.Success -> {
                    val args = Bundle().apply { putString("prefillEmail", email) }
                    findNavController().navigate(R.id.loginFragment, args, null)
                }
                is AppResult.Failure -> {
                    binding.createAccountButton.isEnabled = true
                    binding.createAccountButton.text = getString(R.string.create_account_button)
                    showError(result.error.message)
                }
            }
        }
    }

    private fun showError(message: String) {
        binding.createAccountErrorText.text = message.ifBlank { getString(R.string.create_account_failed) }
        binding.createAccountErrorText.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
