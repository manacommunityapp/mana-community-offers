package com.manacommunity.offers.dto;

import com.manacommunity.offers.domain.enums.CouponDiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCouponRequest {

    @NotBlank(message = "Coupon code is required")
    private String code;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String businessId;

    private String communityId;

    @NotNull(message = "Discount type is required")
    private CouponDiscountType discountType;

    @NotNull(message = "Discount value is required")
    private Double discountValue;

    private Double minOrderAmount;

    private Double maxDiscountAmount;

    private LocalDate validFrom;

    private LocalDate validUntil;

    private Integer usageLimitTotal;

    private Integer usageLimitPerUser;

    private String createdByUserId;
}
