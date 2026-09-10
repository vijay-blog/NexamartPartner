package com.daily.nexamartpartner.features.delivery

import com.daily.nexamartpartner.features.delivery.data.contract.ActiveDeliveryOrderWorkflowContract
import com.daily.nexamartpartner.features.delivery.domain.model.DeliveryOrdersQuery
import org.junit.Assert.assertEquals
import org.junit.Test

class ActiveDeliveryOrderWorkflowContractTest {
    private val contract = ActiveDeliveryOrderWorkflowContract()

    @Test
    fun `buildListQuery uses backend search key and instant filters`() {
        val params = contract.buildListQuery(
            DeliveryOrdersQuery(
                page = 1,
                pageSize = 20,
                searchText = "ORD-10",
                fromDate = "2026-09-10",
                toDate = "2026-09-10"
            )
        )

        assertEquals("ORD-10", params["search"])
        assertEquals("2026-09-10T00:00:00Z", params["fromDate"])
        assertEquals("2026-09-11T00:00:00Z", params["toDate"])
    }
}
