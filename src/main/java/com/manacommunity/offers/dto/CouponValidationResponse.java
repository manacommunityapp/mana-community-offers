package com.manacommunity.offers.dto;

import com.manacommunity.offers.domain.enums.CouponDiscountType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponValidationResponse {

    private boolean valid;
    private String couponId;
    private String code;
    private String message;
    private CouponDiscountType discountType;
    private Double discountValue;
    private Double originalAmount;
    private Double calculatedDiscount;
    private Double finalPayableAmount;
    private Double minOrderAmount;
    private Double maxDiscountAmount;
}
