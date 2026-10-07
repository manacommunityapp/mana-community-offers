package com.manacommunity.offers.entity;

import com.manacommunity.offers.domain.enums.BusinessVerificationStatus;
import com.manacommunity.offers.domain.enums.PartnershipTier;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "businesses")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 100)
    private String registeredEntityName;

    @Column(nullable = false, length = 100)
    private String categoryId;

    @Column(length = 100)
    private String categoryName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String tagline;

    @Column(length = 500)
    private String logoUrl;

    @Column(length = 500)
    private String bannerUrl;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 20)
    private String pincode;

    @Column(length = 20)
    private String phone;

    @Column(length = 120)
    private String email;

    @Column(length = 255)
    private String websiteUrl;

    @Column(length = 255)
    private String googleMapsUrl;

    private Double distanceKm;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BusinessVerificationStatus verificationStatus = BusinessVerificationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PartnershipTier partnershipTier = PartnershipTier.NONE;

    @Builder.Default
    private Double averageRating = 0.0;

    @Builder.Default
    private Integer reviewCount = 0;

    @Builder.Default
    private Integer activeDealsCount = 0;

    @Builder.Default
    private Integer totalRedemptions = 0;

    @Column(length = 20)
    private String gstin;

    @Column(length = 50)
    private String businessRegistrationNumber;

    @Column(length = 500)
    private String kycDocumentUrl;

    @Column(length = 50)
    private String bankAccountNumber;

    @Column(length = 20)
    private String bankIfscCode;

    @Column(length = 150)
    private String bankAccountHolder;

    @Column(length = 100)
    private String bankName;

    @Builder.Default
    private Double commissionRatePct = 5.0;

    @Column(length = 100)
    private String verifiedByUserId;

    private LocalDateTime verifiedAt;

    @Column(length = 255)
    private String rejectionReason;

    @Column(length = 100)
    private String ownerUserId;

    @Builder.Default
    private Boolean active = true;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
