package com.manacommunity.offers.controller;

import com.manacommunity.offers.domain.enums.BusinessVerificationStatus;
import com.manacommunity.offers.domain.enums.OfferStatus;
import com.manacommunity.offers.dto.*;
import com.manacommunity.offers.service.BusinessService;
import com.manacommunity.offers.service.CommerceAnalyticsService;
import com.manacommunity.offers.service.CommunityOfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/offers/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommerceAdminController {

    private final CommerceAnalyticsService analyticsService;
    private final CommunityOfferService offerService;
    private final BusinessService businessService;

    @GetMapping("/analytics")
    public ResponseEntity<CommerceAnalyticsDto> getAnalytics(
            @RequestParam(defaultValue = "comm-mana-residency") String communityId) {
        return ResponseEntity.ok(analyticsService.getCommunityCommerceAnalytics(communityId));
    }

    @GetMapping("/campaign-analytics")
    public ResponseEntity<List<CampaignAnalyticsDto>> getAllCampaignAnalytics() {
        return ResponseEntity.ok(analyticsService.getAllCampaignAnalytics());
    }

    @PostMapping("/deals/{id}/status")
    public ResponseEntity<OfferResponseDto> updateDealStatus(
            @PathVariable String id,
            @RequestParam OfferStatus status,
            @RequestParam(defaultValue = "admin-1") String adminUserId,
            @RequestParam(required = false) String rejectionReason) {
        return ResponseEntity.ok(offerService.updateOfferStatus(id, status, adminUserId, rejectionReason));
    }

    @PostMapping("/businesses/{id}/status")
    public ResponseEntity<BusinessResponseDto> updateBusinessStatus(
            @PathVariable String id,
            @RequestParam BusinessVerificationStatus status,
            @RequestParam(defaultValue = "admin-1") String adminUserId) {
        return ResponseEntity.ok(businessService.updateVerificationStatus(id, status, adminUserId));
    }

    @PostMapping("/businesses/{id}/verify")
    public ResponseEntity<BusinessResponseDto> verifyMerchant(
            @PathVariable String id,
            @Valid @RequestBody VerifyMerchantRequest req) {
        return ResponseEntity.ok(businessService.verifyMerchant(id, req));
    }

    @GetMapping("/businesses/pending-verification")
    public ResponseEntity<List<BusinessResponseDto>> getPendingVerifications() {
        return ResponseEntity.ok(businessService.getPendingVerificationBusinesses());
    }

    @PostMapping("/maintenance/expire-outdated")
    public ResponseEntity<Map<String, Object>> expireOutdatedRecords() {
        int expiredCount = offerService.expireOutdatedRecords();
        return ResponseEntity.ok(Map.of(
                "message", "Expired outdated records successfully",
                "expiredCount", expiredCount
        ));
    }
}
