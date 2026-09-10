package com.daily.nexamartpartner.features.delivery.earnings.data.contract
import com.daily.nexamartpartner.features.delivery.earnings.domain.model.DeliveryEarningsQuery
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class ActiveDeliveryEarningsContract : DeliveryEarningsContract {
 override val summaryPath = "delivery/earnings/summary"
 override val historyPath = "delivery/earnings"

 override fun buildSummaryQuery(query: DeliveryEarningsQuery): Map<String, String> = buildMap {
  normalizeFromDate(query.fromDate)?.let { put("fromDate", it) }
  normalizeToDateExclusive(query.toDate)?.let { put("toDate", it) }
 }

 override fun buildHistoryQuery(query: DeliveryEarningsQuery): Map<String, String> = buildMap {
  put("page", query.page.toString())
  put("pageSize", query.pageSize.toString())
  normalizeFromDate(query.fromDate)?.let { put("fromDate", it) }
  normalizeToDateExclusive(query.toDate)?.let { put("toDate", it) }
 }

 private fun normalizeFromDate(raw: String?): String? {
  val value = raw?.trim()?.takeIf { it.isNotEmpty() } ?: return null
  if (value.contains("T")) return value
  val parsed = runCatching { dateParser.parse(value) }.getOrNull() ?: return null
  return instantFormatter.format(parsed)
 }

 private fun normalizeToDateExclusive(raw: String?): String? {
  val value = raw?.trim()?.takeIf { it.isNotEmpty() } ?: return null
  if (value.contains("T")) return value
  val parsed = runCatching { dateParser.parse(value) }.getOrNull() ?: return null
  val cal = Calendar.getInstance(utc).apply { time = parsed; add(Calendar.DAY_OF_MONTH, 1) }
  return instantFormatter.format(cal.time)
 }

 companion object {
  private val utc = TimeZone.getTimeZone("UTC")
  private val dateParser = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
   timeZone = utc
   isLenient = false
  }
  private val instantFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
   timeZone = utc
  }
 }
}
