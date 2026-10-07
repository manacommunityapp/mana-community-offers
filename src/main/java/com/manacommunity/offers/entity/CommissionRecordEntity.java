package com.manacommunity.offers.entity;

import com.manacommunity.offers.domain.enums.CommissionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_commissions", indexes = {
    @Index(name = "idx_comm_biz_status", columnList = "businessId, status"),
    @Index(name = "idx_comm_settlement", columnList = "settlementBatchId")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommissionRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100)
    private String businessId;

    @Column(nullable = false, length = 150)
    private String businessName;

    @Column(nullable = false, length = 100)
    private String offerId;

    @Column(length = 200)
    private String offerTitle;

    @Column(length = 100)
    private String claimId;

    @Column(length = 40)
    private String redemptionCode;

    @Column(length = 100)
    private String residentUserId;

    private Double billAmount; // Gross amount billed

    private Double discountAmount; // Savings given to resident

    private Double commissionRatePct; // e.g. 5.0%

    private Double commissionAmount; // Platform fee = billAmount * rate

    private Double netMerchantAmount; // billAmount - commissionAmount

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CommissionStatus status = CommissionStatus.PENDING_SETTLEMENT;

    @Column(length = 100)
    private String settlementBatchId; // linked once batched

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
