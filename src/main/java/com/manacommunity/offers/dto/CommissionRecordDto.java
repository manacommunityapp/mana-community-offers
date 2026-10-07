package com.manacommunity.offers.dto;

import com.manacommunity.offers.domain.enums.CommissionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommissionRecordDto {

    private String id;
    private String businessId;
    private String businessName;
    private String offerId;
    private String offerTitle;
    private String claimId;
    private String redemptionCode;
    private String residentUserId;
    private Double billAmount;
    private Double discountAmount;
    private Double commissionRatePct;
    private Double commissionAmount;
    private Double netMerchantAmount;
    private CommissionStatus status;
    private String settlementBatchId;
    private LocalDateTime createdAt;
}
