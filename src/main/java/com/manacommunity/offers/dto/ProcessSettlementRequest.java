package com.manacommunity.offers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessSettlementRequest {

    @NotBlank(message = "Payout UTR / Reference is required")
    private String payoutReference;

    private String adminUserId;
    private String notes;
}
