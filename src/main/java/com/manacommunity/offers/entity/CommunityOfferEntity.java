package com.manacommunity.offers.entity;

import com.manacommunity.offers.domain.enums.DealType;
import com.manacommunity.offers.domain.enums.OfferStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "community_offers")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityOfferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100)
    private String businessId;

    @Column(nullable = false, length = 150)
    private String businessName;

    @Column(length = 500)
    private String businessLogoUrl;

    @Column(nullable = false, length = 100)
    private String categoryId;

    @Column(length = 100)
    private String categoryName;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 255)
    private String tagline;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String coverImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DealType dealType = DealType.DISCOUNT;

    private Double regularPrice;

    private Double communityPrice;

    private Double discountPercentage;

    @Column(length = 100)
    private String savingsSummary; // e.g. "Save ₹400 (40% OFF)"

    @Column(columnDefinition = "TEXT")
    private String termsAndConditions;

    @Column(length = 255)
    private String eligibilityNote; // e.g. "Exclusive to Mana Residency & Green Valley residents"

    // Multi-community targeting as comma-separated IDs or JSON
    @Column(columnDefinition = "TEXT")
    private String targetCommunityIds; // e.g. "comm-mana-residency,comm-green-valley"

    @Column(columnDefinition = "TEXT")
    private String targetCommunityNames;

    @Builder.Default
    private Integer estimatedAudience = 0;

    private LocalDate validFrom;

    private LocalDate validUntil;

    @Builder.Default
    private Integer maxClaims = 100;

    @Builder.Default
    private Integer claimedCount = 0;

    @Builder.Default
    private Integer redeemedCount = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OfferStatus status = OfferStatus.DRAFT;

    @Column(length = 100)
    private String approvedByUserId;

    private LocalDateTime approvedAt;

    @Column(length = 255)
    private String rejectionReason;

    @Builder.Default
    private Boolean featured = false;

    @Builder.Default
    private Integer viewCount = 0;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
