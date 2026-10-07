package com.manacommunity.offers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateCouponRequest {

    @NotBlank(message = "Coupon code is required")
    private String code;

    @NotNull(message = "Order/bill amount is required")
    private Double orderAmount;

    @NotBlank(message = "Resident user ID is required")
    private String residentUserId;

    private String businessId;

    private String claimId;
}
