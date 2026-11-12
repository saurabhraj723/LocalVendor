package com.localflow.dashboard.service;

import com.localflow.business.domain.Business;
import com.localflow.business.repository.BusinessRepository;
import com.localflow.common.exception.NotFoundException;
import com.localflow.common.security.CurrentUserProvider;
import com.localflow.customer.repository.CustomerRepository;
import com.localflow.dashboard.api.dto.DashboardSummaryResponse;
import com.localflow.dashboard.api.dto.RecentOrderResponse;
import com.localflow.order.domain.OrderStatus;
import com.localflow.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class DashboardService {

    private static final List<OrderStatus> PENDING_STATUSES = List.of(
            OrderStatus.NEW,
            OrderStatus.CONFIRMED,
            OrderStatus.PACKING,
            OrderStatus.READY,
            OrderStatus.OUT_FOR_DELIVERY,
            OrderStatus.PICKUP_READY
    );
    private static final List<OrderStatus> NON_SALES_STATUSES = List.of(
            OrderStatus.REJECTED,
            OrderStatus.CANCELLED
    );

    private final BusinessRepository businessRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final CurrentUserProvider currentUserProvider;

    public DashboardService(
            BusinessRepository businessRepository,
            OrderRepository orderRepository,
            CustomerRepository customerRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.businessRepository = businessRepository;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse summary() {
        Long businessId = currentUserProvider.currentBusinessId();
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new NotFoundException("BUSINESS_NOT_FOUND", "Business was not found."));
        ZoneId zone = ZoneId.of(business.getTimezone());
        LocalDate businessDate = LocalDate.now(zone);
        OffsetDateTime start = businessDate.atStartOfDay(zone).toOffsetDateTime();
        OffsetDateTime end = businessDate.plusDays(1).atStartOfDay(zone).toOffsetDateTime();

        BigDecimal todaySales = orderRepository.sumOrderValueForPeriod(
                businessId, start, end, NON_SALES_STATUSES);
        List<RecentOrderResponse> recentOrders = orderRepository
                .findTop10ByBusiness_IdOrderByCreatedAtDesc(businessId)
                .stream().map(RecentOrderResponse::from).toList();

        return new DashboardSummaryResponse(
                businessDate,
                business.getCurrency(),
                orderRepository.countByBusiness_IdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                        businessId, start, end),
                todaySales == null ? BigDecimal.ZERO : todaySales,
                orderRepository.countByBusiness_IdAndStatusIn(businessId, PENDING_STATUSES),
                customerRepository.countByBusiness_IdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                        businessId, start, end),
                recentOrders
        );
    }
}
