package com.localflow.dashboard.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardSummaryResponse(
        LocalDate businessDate,
        String currency,
        long todayOrderCount,
        BigDecimal todaySales,
        long pendingOrderCount,
        long newCustomerCount,
        List<RecentOrderResponse> recentOrders
) {
}
