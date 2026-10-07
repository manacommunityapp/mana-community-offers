package com.manacommunity.offers.dto;

import com.manacommunity.offers.domain.enums.DealType;
import com.manacommunity.offers.domain.enums.OfferStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferResponseDto {

    private String id;
    private String businessId;
    private String businessName;
    private String businessLogoUrl;
    private String categoryId;
    private String categoryName;
    private String title;
    private String tagline;
    private String description;
    private String coverImageUrl;
    private DealType dealType;
    private Double regularPrice;
    private Double communityPrice;
    private Double discountPercentage;
    private String savingsSummary;
    private String termsAndConditions;
    private String eligibilityNote;
    private List<String> targetCommunityIds;
    private List<String> targetCommunityNames;
    private Integer estimatedAudience;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private Integer maxClaims;
    private Integer maxClaimsPerUser;
    private Double minOrderAmount;
    private Double commissionRateOverridePct;
    private Integer claimedCount;
    private Integer redeemedCount;
    private Integer availableClaims;
    private Boolean isClaimable;
    private OfferStatus status;
    private Boolean featured;
    private Integer viewCount;
    private LocalDateTime createdAt;
}
