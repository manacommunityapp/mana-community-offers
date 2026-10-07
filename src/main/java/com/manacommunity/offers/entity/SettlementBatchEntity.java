package com.manacommunity.offers.entity;

import com.manacommunity.offers.domain.enums.SettlementStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "community_settlement_batches", indexes = {
    @Index(name = "idx_settle_biz", columnList = "businessId"),
    @Index(name = "idx_settle_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementBatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 60)
    private String settlementNumber; // e.g. "SETTLE-202610-001"

    @Column(nullable = false, length = 100)
    private String businessId;

    @Column(nullable = false, length = 150)
    private String businessName;

    private LocalDate periodStart;

    private LocalDate periodEnd;

    @Builder.Default
    private Integer totalRedemptions = 0;

    @Builder.Default
    private Double grossSalesAmount = 0.0;

    @Builder.Default
    private Double totalCommissionAmount = 0.0;

    @Builder.Default
    private Double netPayoutAmount = 0.0;

    @Column(length = 50)
    private String bankAccountNumber;

    @Column(length = 20)
    private String bankIfscCode;

    @Column(length = 150)
    private String bankAccountHolder;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SettlementStatus status = SettlementStatus.PENDING;

    @Column(length = 100)
    private String payoutReference; // UTR or Bank transaction reference

    private LocalDateTime settledAt;

    @Column(length = 100)
    private String settledByUserId;

    @Column(length = 500)
    private String notes;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
