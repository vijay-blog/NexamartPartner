package com.daily.nexamartpartner.features.admin.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.databinding.FragmentAdminDashboardPlaceholderBinding
import com.daily.nexamartpartner.di.appContainer
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModel
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModelFactory
import com.daily.nexamartpartner.routing.ProtectedNavigator

class AdminDashboardPlaceholderFragment : Fragment(R.layout.fragment_admin_dashboard_placeholder) {
    private var _binding: FragmentAdminDashboardPlaceholderBinding? = null
    private val binding: FragmentAdminDashboardPlaceholderBinding
        get() = requireNotNull(_binding)

    private val authCoordinatorViewModel: AuthCoordinatorViewModel by activityViewModels {
        AuthCoordinatorViewModelFactory(
            restoreSessionUseCase = requireContext().appContainer.restoreSessionUseCase,
            logoutUseCase = requireContext().appContainer.logoutUseCase,
            authStateStore = requireContext().appContainer.authStateStore
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdminDashboardPlaceholderBinding.bind(view)
        val navigator = ProtectedNavigator(
            navController = findNavController(),
            authStateStore = requireContext().appContainer.authStateStore
        )
        binding.ordersButton.setOnClickListener { navigator.navigate(R.id.adminOrdersPlaceholderFragment) }
        binding.productsButton.setOnClickListener { navigator.navigate(R.id.adminProductsPlaceholderFragment) }
        binding.categoriesButton.setOnClickListener { navigator.navigate(R.id.adminCategoriesPlaceholderFragment) }
        binding.customersButton.setOnClickListener { navigator.navigate(R.id.adminCustomersPlaceholderFragment) }
        binding.deliveryPartnersButton.setOnClickListener { navigator.navigate(R.id.adminDeliveryPartnersPlaceholderFragment) }
        binding.reportsButton.setOnClickListener { navigator.navigate(R.id.adminReportsPlaceholderFragment) }
        binding.notificationsButton.setOnClickListener { navigator.navigate(R.id.adminNotificationsPlaceholderFragment) }
        binding.profileButton.setOnClickListener { navigator.navigate(R.id.adminProfilePlaceholderFragment) }
        binding.settingsButton.setOnClickListener { navigator.navigate(R.id.adminSettingsPlaceholderFragment) }
        binding.logoutButton.setOnClickListener {
            authCoordinatorViewModel.logout()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
