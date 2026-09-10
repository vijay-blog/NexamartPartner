package com.daily.nexamartpartner.features.delivery.presentation.state

import com.daily.nexamartpartner.features.delivery.domain.model.DeliveryOrderSummary
import com.daily.nexamartpartner.features.delivery.presentation.viewmodel.DeliveryHistoryViewModel

data class DeliveryHistoryUiState(
    val filters: DeliveryHistoryViewModel.Filters = DeliveryHistoryViewModel.Filters(),
    val isRefreshing:Boolean=false,
    val isLoadingMore:Boolean=false,
    val content:Content=Content.Loading
){
 sealed interface Content { data object Loading:Content; data class Success(val orders:List<DeliveryOrderSummary>,val hasNext:Boolean):Content; data class Empty(val title:String,val message:String):Content; data class Error(val title:String,val message:String):Content; data class Unavailable(val title:String,val message:String):Content }
}
