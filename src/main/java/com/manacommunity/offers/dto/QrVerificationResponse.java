package com.manacommunity.offers.dto;

import com.manacommunity.offers.domain.enums.ClaimStatus;
import com.manacommunity.offers.domain.enums.DealType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrVerificationResponse {

    private boolean valid;
    private String claimId;
    private String redemptionCode;
    private String counterPin;
    private ClaimStatus status;
    private String offerId;
    private String offerTitle;
    private String businessId;
    private String businessName;
    private String residentUserId;
    private String residentName;
    private String unitNumber;
    private DealType dealType;
    private Double regularPrice;
    private Double communityPrice;
    private String savingsSummary;
    private LocalDate validUntil;
    private boolean alreadyRedeemed;
    private boolean isExpired;
    private String message;
}
