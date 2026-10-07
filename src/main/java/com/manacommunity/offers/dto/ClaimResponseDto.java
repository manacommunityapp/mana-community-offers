package com.manacommunity.offers.dto;

import com.manacommunity.offers.domain.enums.ClaimStatus;
import com.manacommunity.offers.domain.enums.DealType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimResponseDto {

    private String id;
    private String offerId;
    private String offerTitle;
    private DealType dealType;
    private Double regularPrice;
    private Double communityPrice;
    private String savingsSummary;
    private String businessId;
    private String businessName;
    private String businessLogoUrl;
    private String businessAddress;
    private String businessPhone;
    private String communityId;
    private String residentUserId;
    private String residentName;
    private String unitNumber;
    private String redemptionCode; // e.g. "MANA-8F29K"
    private String qrPayload;
    private String counterPin;
    private ClaimStatus status;
    private LocalDate validUntil;
    private LocalDateTime claimedAt;
    private LocalDateTime redeemedAt;
    private Double billAmount;
    private Double discountAmount;
    private Double commissionAmount;
}
