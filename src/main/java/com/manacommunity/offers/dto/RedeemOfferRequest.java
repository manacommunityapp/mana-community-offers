package com.manacommunity.offers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedeemOfferRequest {

    private String redemptionCode; // e.g. "MANA-8F29K" (or extracted from qrPayload)
    private String qrPayload; // Scanned QR string
    private String counterPin; // 4-digit backup PIN

    private String businessId;

    private Double billAmount; // Gross invoice amount for commission calculation
    private String staffName;
    private String notes;
}
