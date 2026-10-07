package com.manacommunity.offers.dto;

import com.manacommunity.offers.domain.enums.SettlementStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementBatchDto {

    private String id;
    private String settlementNumber;
    private String businessId;
    private String businessName;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Integer totalRedemptions;
    private Double grossSalesAmount;
    private Double totalCommissionAmount;
    private Double netPayoutAmount;
    private String bankAccountNumber;
    private String bankIfscCode;
    private String bankAccountHolder;
    private SettlementStatus status;
    private String payoutReference;
    private LocalDateTime settledAt;
    private String settledByUserId;
    private String notes;
    private LocalDateTime createdAt;
}
