package com.manacommunity.offers.dto;

import com.manacommunity.offers.domain.enums.DealType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOfferRequest {

    @NotBlank(message = "Business ID is required")
    private String businessId;

    @NotBlank(message = "Category ID is required")
    private String categoryId;

    @NotBlank(message = "Offer title is required")
    private String title;

    private String tagline;
    private String description;
    private String coverImageUrl;

    @NotNull(message = "Deal type is required")
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
    private Boolean featured;
}
