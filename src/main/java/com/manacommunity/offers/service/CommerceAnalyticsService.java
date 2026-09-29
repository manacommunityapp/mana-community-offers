package com.manacommunity.offers.service;

import com.manacommunity.offers.domain.enums.ClaimStatus;
import com.manacommunity.offers.domain.enums.OfferStatus;
import com.manacommunity.offers.dto.CommerceAnalyticsDto;
import com.manacommunity.offers.entity.CommunityOfferEntity;
import com.manacommunity.offers.repository.BusinessRepository;
import com.manacommunity.offers.repository.CommunityMarketEventRepository;
import com.manacommunity.offers.repository.CommunityOfferRepository;
import com.manacommunity.offers.repository.OfferClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommerceAnalyticsService {

    private final BusinessRepository businessRepository;
    private final CommunityOfferRepository offerRepository;
    private final CommunityMarketEventRepository eventRepository;
    private final OfferClaimRepository claimRepository;

    @Transactional(readOnly = true)
    public CommerceAnalyticsDto getCommunityCommerceAnalytics(String communityId) {
        long businesses = businessRepository.count();
        List<CommunityOfferEntity> activeOffers = offerRepository.findActiveOffersForCommunity(communityId, LocalDate.now());
        long events = eventRepository.count();
        long totalClaims = claimRepository.count();
        long totalRedemptions = claimRepository.findByResidentUserIdAndStatus("", ClaimStatus.REDEEMED).size();

        double savings = activeOffers.stream()
                .filter(o -> o.getRegularPrice() != null && o.getCommunityPrice() != null)
                .mapToDouble(o -> (o.getRegularPrice() - o.getCommunityPrice()) * (o.getRedeemedCount() != null ? o.getRedeemedCount() : o.getClaimedCount()))
                .sum();

        Map<String, Long> categoryDistribution = new HashMap<>();
        Map<String, Long> dealTypeDistribution = new HashMap<>();

        for (CommunityOfferEntity o : activeOffers) {
            String cat = o.getCategoryName() != null ? o.getCategoryName() : "Other";
            categoryDistribution.put(cat, categoryDistribution.getOrDefault(cat, 0L) + 1);

            String deal = o.getDealType() != null ? o.getDealType().name() : "DISCOUNT";
            dealTypeDistribution.put(deal, dealTypeDistribution.getOrDefault(deal, 0L) + 1);
        }

        double rate = totalClaims > 0 ? ((double) totalRedemptions / totalClaims) * 100.0 : 0.0;

        return CommerceAnalyticsDto.builder()
                .totalActiveBusinesses(businesses)
                .totalActiveOffers((long) activeOffers.size())
                .totalMarketEvents(events)
                .totalClaims(totalClaims)
                .totalRedemptions(totalRedemptions)
                .redemptionRate(Math.round(rate * 10.0) / 10.0)
                .totalEstimatedSavings(savings)
                .categoryDistribution(categoryDistribution)
                .dealTypeDistribution(dealTypeDistribution)
                .build();
    }
}
