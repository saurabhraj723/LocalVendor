package com.localflow.order.repository;

import com.localflow.order.domain.CustomerOrder;
import com.localflow.order.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

    Page<CustomerOrder> findAllByBusiness_IdOrderByCreatedAtDesc(Long businessId, Pageable pageable);

    Page<CustomerOrder> findAllByCustomer_IdAndBusiness_IdOrderByCreatedAtDesc(
            Long customerId,
            Long businessId,
            Pageable pageable
    );

    Optional<CustomerOrder> findByIdAndBusiness_Id(Long id, Long businessId);

    Optional<CustomerOrder> findByIdAndCustomer_IdAndBusiness_Id(Long id, Long customerId, Long businessId);

    Optional<CustomerOrder> findByBusiness_IdAndWhatsappMessageId(Long businessId, String whatsappMessageId);

    Optional<CustomerOrder> findByBusiness_IdAndCustomer_IdAndStorefrontRequestId(
            Long businessId, Long customerId, String storefrontRequestId);

    long countByBusiness_IdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            Long businessId,
            OffsetDateTime start,
            OffsetDateTime end
    );

    long countByBusiness_IdAndStatusIn(Long businessId, List<OrderStatus> statuses);

    @Query("select coalesce(sum(o.totalAmount), 0) from CustomerOrder o " +
            "where o.business.id = :businessId and o.createdAt >= :start and o.createdAt < :end " +
            "and o.status not in :excludedStatuses")
    BigDecimal sumOrderValueForPeriod(
            @Param("businessId") Long businessId,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("excludedStatuses") List<OrderStatus> excludedStatuses
    );

    @EntityGraph(attributePaths = "customer")
    List<CustomerOrder> findTop10ByBusiness_IdOrderByCreatedAtDesc(Long businessId);
}
