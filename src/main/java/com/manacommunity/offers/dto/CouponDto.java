package com.manacommunity.offers.dto;

import com.manacommunity.offers.domain.enums.CouponDiscountType;
import com.manacommunity.offers.domain.enums.CouponStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDto {

    private String id;
    private String code;
    private String title;
    private String description;
    private String businessId;
    private String businessName;
    private String communityId;
    private CouponDiscountType discountType;
    private Double discountValue;
    private Double minOrderAmount;
    private Double maxDiscountAmount;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private Integer usageLimitTotal;
    private Integer usageLimitPerUser;
    private Integer totalUsedCount;
    private CouponStatus status;
    private Boolean active;
    private LocalDateTime createdAt;
}
