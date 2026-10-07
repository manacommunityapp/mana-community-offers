package com.manacommunity.offers.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignAnalyticsDto {

    private String offerId;
    private String offerTitle;
    private String businessId;
    private String businessName;
    private String categoryName;
    private Integer viewCount;
    private Integer claimedCount;
    private Integer redeemedCount;
    private Integer availableClaims;
    private Double claimRatePct; // claimed / views
    private Double redemptionRatePct; // redeemed / claimed
    private Double totalGmvDiscounted; // resident savings
    private Double totalSalesGmv; // gross transaction value
    private Double platformCommissionEarned;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private boolean active;
}
