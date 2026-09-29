package com.manacommunity.offers.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommerceAnalyticsDto {

    private Long totalActiveBusinesses;
    private Long totalActiveOffers;
    private Long totalMarketEvents;
    private Long totalClaims;
    private Long totalRedemptions;
    private Double redemptionRate;
    private Double totalEstimatedSavings;
    private Map<String, Long> categoryDistribution;
    private Map<String, Long> dealTypeDistribution;
}
