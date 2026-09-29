package com.manacommunity.offers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedeemOfferRequest {

    @NotBlank(message = "Redemption code is required")
    private String redemptionCode; // e.g. "MANA-8F29K"

    @NotBlank(message = "Business ID is required")
    private String businessId;

    private String staffName;
    private String notes;
}
