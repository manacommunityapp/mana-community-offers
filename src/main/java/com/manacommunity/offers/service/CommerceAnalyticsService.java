package com.manacommunity.offers.service;

import com.manacommunity.offers.domain.enums.ClaimStatus;
import com.manacommunity.offers.domain.enums.CommissionStatus;
import com.manacommunity.offers.domain.enums.SettlementStatus;
import com.manacommunity.offers.dto.CampaignAnalyticsDto;
import com.manacommunity.offers.dto.CommerceAnalyticsDto;
import com.manacommunity.offers.entity.*;
import com.manacommunity.offers.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommerceAnalyticsService {

    private final BusinessRepository businessRepository;
    private final CommunityOfferRepository offerRepository;
    private final CommunityMarketEventRepository eventRepository;
    private final OfferClaimRepository claimRepository;
    private final CommissionRecordRepository commissionRepository;
    private final SettlementBatchRepository settlementRepository;
    private final CommunityOfferService offerService;

    @Transactional(readOnly = true)
    public CommerceAnalyticsDto getCommunityCommerceAnalytics(String communityId) {
        long businesses = businessRepository.count();
        List<CommunityOfferEntity> activeOffers = offerRepository.findActiveOffersForCommunity(communityId, LocalDate.now());
        long events = eventRepository.count();
        long totalClaims = claimRepository.count();
        long totalRedemptions = claimRepository.findAll().stream()
                .filter(c -> c.getStatus() == ClaimStatus.REDEEMED)
                .count();

        double savings = activeOffers.stream()
                .filter(o -> o.getRegularPrice() != null && o.getCommunityPrice() != null)
                .mapToDouble(o -> (o.getRegularPrice() - o.getCommunityPrice()) * (o.getRedeemedCount() != null ? o.getRedeemedCount() : o.getClaimedCount()))
                .sum();

        List<CommissionRecordEntity> commissions = commissionRepository.findAll();
        double totalCommissions = commissions.stream()
                .mapToDouble(CommissionRecordEntity::getCommissionAmount)
                .sum();

        List<SettlementBatchEntity> settlements = settlementRepository.findAll();
        double settledPayouts = settlements.stream()
                .filter(s -> s.getStatus() == SettlementStatus.SETTLED)
                .mapToDouble(SettlementBatchEntity::getNetPayoutAmount)
                .sum();

        double pendingPayouts = settlements.stream()
                .filter(s -> s.getStatus() == SettlementStatus.PENDING || s.getStatus() == SettlementStatus.PROCESSING)
                .mapToDouble(SettlementBatchEntity::getNetPayoutAmount)
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
                .totalEstimatedSavings(Math.round(savings * 100.0) / 100.0)
                .totalCommissionsEarned(Math.round(totalCommissions * 100.0) / 100.0)
                .totalSettledPayouts(Math.round(settledPayouts * 100.0) / 100.0)
                .totalPendingPayouts(Math.round(pendingPayouts * 100.0) / 100.0)
                .categoryDistribution(categoryDistribution)
                .dealTypeDistribution(dealTypeDistribution)
                .build();
    }

    @Transactional(readOnly = true)
    public List<CampaignAnalyticsDto> getAllCampaignAnalytics() {
        return offerRepository.findAll().stream()
                .map(o -> offerService.getCampaignAnalytics(o.getId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CampaignAnalyticsDto> getBusinessCampaignAnalytics(String businessId) {
        return offerRepository.findByBusinessId(businessId).stream()
                .map(o -> offerService.getCampaignAnalytics(o.getId()))
                .collect(Collectors.toList());
    }
}
