package com.daily.nexamartpartner.features.delivery

import com.daily.nexamartpartner.features.delivery.earnings.data.contract.ActiveDeliveryEarningsContract
import com.daily.nexamartpartner.features.delivery.earnings.domain.model.DeliveryEarningsQuery
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ActiveDeliveryEarningsContractTest {
    private val contract = ActiveDeliveryEarningsContract()

    @Test
    fun `history path matches backend endpoint`() {
        assertEquals("delivery/earnings", contract.historyPath)
    }

    @Test
    fun `buildSummaryQuery omits blank values and normalizes date bounds`() {
        val empty = contract.buildSummaryQuery(DeliveryEarningsQuery())
        assertFalse(empty.containsKey("fromDate"))
        assertFalse(empty.containsKey("toDate"))

        val withDates = contract.buildSummaryQuery(
            DeliveryEarningsQuery(fromDate = "2026-09-10", toDate = "2026-09-10")
        )
        assertEquals("2026-09-10T00:00:00Z", withDates["fromDate"])
        assertEquals("2026-09-11T00:00:00Z", withDates["toDate"])
    }
}
