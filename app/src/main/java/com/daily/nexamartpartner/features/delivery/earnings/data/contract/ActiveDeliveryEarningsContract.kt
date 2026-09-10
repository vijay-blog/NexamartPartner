package com.daily.nexamartpartner.features.delivery.earnings.data.contract
import com.daily.nexamartpartner.features.delivery.earnings.domain.model.DeliveryEarningsQuery
class ActiveDeliveryEarningsContract:DeliveryEarningsContract{
 override val summaryPath="delivery/earnings/summary";override val historyPath="delivery/earnings/history"
 override fun buildSummaryQuery(q:DeliveryEarningsQuery)=mapOf("fromDate" to (q.fromDate ?: ""),"toDate" to (q.toDate ?: ""))
 override fun buildHistoryQuery(q:DeliveryEarningsQuery)=mapOf("page" to q.page.toString(),"pageSize" to q.pageSize.toString(),"fromDate" to (q.fromDate ?: ""),"toDate" to (q.toDate ?: ""))
}
