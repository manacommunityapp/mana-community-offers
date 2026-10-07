package com.manacommunity.offers.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_coupon_usages", indexes = {
    @Index(name = "idx_usage_coupon_user", columnList = "couponId, residentUserId"),
    @Index(name = "idx_usage_user", columnList = "residentUserId")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponUsageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100)
    private String couponId;

    @Column(nullable = false, length = 40)
    private String couponCode;

    @Column(nullable = false, length = 100)
    private String residentUserId;

    @Column(length = 150)
    private String residentName;

    @Column(length = 100)
    private String businessId;

    @Column(length = 100)
    private String claimId;

    private Double orderAmount;

    private Double discountApplied;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime usedAt;
}
