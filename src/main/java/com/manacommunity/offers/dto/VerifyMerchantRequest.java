package com.manacommunity.offers.dto;

import com.manacommunity.offers.domain.enums.BusinessVerificationStatus;
import com.manacommunity.offers.domain.enums.PartnershipTier;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyMerchantRequest {

    @NotNull(message = "Verification status is required")
    private BusinessVerificationStatus status;

    private PartnershipTier partnershipTier;

    private Double commissionRatePct;

    private String adminUserId;

    private String rejectionReason;
}
