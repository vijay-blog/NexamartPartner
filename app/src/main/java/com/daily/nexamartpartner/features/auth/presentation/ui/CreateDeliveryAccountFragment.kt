package com.daily.nexamartpartner.features.auth.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.databinding.FragmentCreateDeliveryAccountBinding
import com.daily.nexamartpartner.features.auth.local.LocalCredentialStore

class CreateDeliveryAccountFragment : Fragment(R.layout.fragment_create_delivery_account) {
    private var _binding: FragmentCreateDeliveryAccountBinding? = null
    private val binding get() = requireNotNull(_binding)
    private lateinit var credentialStore: LocalCredentialStore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateDeliveryAccountBinding.bind(view)
        credentialStore = LocalCredentialStore(requireContext())
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
            credentialStore.hasDeliveryAccount() -> showError(getString(R.string.create_account_existing_error))
            else -> {
                val result = credentialStore.createDeliveryAccount(name, email, password.toCharArray())
                result.onSuccess {
                    val args = Bundle().apply { putString("prefillEmail", email) }
                    findNavController().navigate(R.id.loginFragment, args, null)
                }.onFailure { showError(it.message ?: getString(R.string.create_account_failed)) }
            }
        }
    }

    private fun showError(message: String) {
        binding.createAccountErrorText.text = message
        binding.createAccountErrorText.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
