package com.daily.nexamartpartner.features.auth.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.databinding.FragmentAuthHomeBinding

class AuthHomeFragment : Fragment(R.layout.fragment_auth_home) {
    private var _binding: FragmentAuthHomeBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAuthHomeBinding.bind(view)
        binding.adminLoginButton.setOnClickListener {
            findNavController().navigate(R.id.adminLoginFragment, null, null)
        }
        binding.deliveryLoginButton.setOnClickListener {
            findNavController().navigate(R.id.loginFragment, null, null)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
