package com.manacommunity.offers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimOfferRequest {

    @NotBlank(message = "Offer ID is required")
    private String offerId;

    @NotBlank(message = "Community ID is required")
    private String communityId;

    @NotBlank(message = "Resident User ID is required")
    private String residentUserId;

    private String residentName;
    private String unitNumber;
}
