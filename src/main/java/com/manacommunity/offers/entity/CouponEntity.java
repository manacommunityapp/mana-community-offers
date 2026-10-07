package com.manacommunity.offers.entity;

import com.manacommunity.offers.domain.enums.CouponDiscountType;
import com.manacommunity.offers.domain.enums.CouponStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "community_coupons", indexes = {
    @Index(name = "idx_coupon_code", columnList = "code", unique = true),
    @Index(name = "idx_coupon_biz", columnList = "businessId")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String businessId; // null or empty means platform-wide / multi-merchant

    @Column(length = 150)
    private String businessName;

    @Column(length = 100)
    private String communityId; // null or "ALL" for all communities

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CouponDiscountType discountType = CouponDiscountType.PERCENTAGE;

    @Column(nullable = false)
    private Double discountValue; // percentage (e.g. 20.0) or flat amount (e.g. 100.0)

    private Double minOrderAmount; // minimum cart/bill value

    private Double maxDiscountAmount; // cap for percentage discount

    private LocalDate validFrom;

    private LocalDate validUntil;

    @Builder.Default
    private Integer usageLimitTotal = 500; // total global redemptions allowed

    @Builder.Default
    private Integer usageLimitPerUser = 1; // redemptions per user

    @Builder.Default
    private Integer totalUsedCount = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CouponStatus status = CouponStatus.ACTIVE;

    @Builder.Default
    private Boolean active = true;

    @Column(length = 100)
    private String createdByUserId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
