package com.daily.nexamartpartner.routing

import com.daily.nexamartpartner.R

enum class DestinationScope {
    PUBLIC,
    ADMIN,
    DELIVERY
}

enum class AppDestination(val navId: Int, val scope: DestinationScope) {
    SPLASH(R.id.splashFragment, DestinationScope.PUBLIC),
    AUTH_GRAPH(R.id.authGraph, DestinationScope.PUBLIC),
    LOGIN(R.id.loginFragment, DestinationScope.PUBLIC),
    ACCESS_DENIED(R.id.unsupportedRoleFragment, DestinationScope.PUBLIC),

    ADMIN_GRAPH(R.id.adminGraph, DestinationScope.ADMIN),
    ADMIN_DASHBOARD(R.id.adminDashboardFragment, DestinationScope.ADMIN),
    ADMIN_ORDERS(R.id.adminOrdersFragment, DestinationScope.ADMIN),
    ADMIN_ORDER_DETAILS(R.id.adminOrderDetailsFragment, DestinationScope.ADMIN),
    ADMIN_PRODUCTS(R.id.adminProductsPlaceholderFragment, DestinationScope.ADMIN),
    ADMIN_CATEGORIES(R.id.adminCategoriesPlaceholderFragment, DestinationScope.ADMIN),
    ADMIN_CUSTOMERS(R.id.adminCustomersPlaceholderFragment, DestinationScope.ADMIN),
    ADMIN_DELIVERY_PARTNERS(R.id.adminDeliveryPartnersPlaceholderFragment, DestinationScope.ADMIN),
    ADMIN_REPORTS(R.id.adminReportsPlaceholderFragment, DestinationScope.ADMIN),
    ADMIN_NOTIFICATIONS(R.id.adminNotificationsPlaceholderFragment, DestinationScope.ADMIN),
    ADMIN_PROFILE(R.id.adminProfilePlaceholderFragment, DestinationScope.ADMIN),
    ADMIN_SETTINGS(R.id.adminSettingsPlaceholderFragment, DestinationScope.ADMIN),

    DELIVERY_GRAPH(R.id.deliveryGraph, DestinationScope.DELIVERY),
    DELIVERY_DASHBOARD(R.id.deliveryDashboardPlaceholderFragment, DestinationScope.DELIVERY),
    DELIVERY_ASSIGNED_ORDERS(R.id.deliveryAssignedOrdersPlaceholderFragment, DestinationScope.DELIVERY),
    DELIVERY_ORDER_DETAILS(R.id.deliveryOrderDetailsPlaceholderFragment, DestinationScope.DELIVERY),
    DELIVERY_PICKUP(R.id.deliveryPickupPlaceholderFragment, DestinationScope.DELIVERY),
    DELIVERY_RUN(R.id.deliveryRunPlaceholderFragment, DestinationScope.DELIVERY),
    DELIVERY_HISTORY(R.id.deliveryHistoryPlaceholderFragment, DestinationScope.DELIVERY),
    DELIVERY_EARNINGS(R.id.deliveryEarningsPlaceholderFragment, DestinationScope.DELIVERY),
    DELIVERY_PROFILE(R.id.deliveryProfilePlaceholderFragment, DestinationScope.DELIVERY),
    DELIVERY_AVAILABILITY(R.id.deliveryAvailabilityPlaceholderFragment, DestinationScope.DELIVERY),
    DELIVERY_NOTIFICATIONS(R.id.deliveryNotificationsPlaceholderFragment, DestinationScope.DELIVERY);

    companion object {
        fun fromNavId(navId: Int): AppDestination? = entries.firstOrNull { it.navId == navId }
    }
}
