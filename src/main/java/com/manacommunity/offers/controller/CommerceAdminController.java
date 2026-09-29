package com.manacommunity.offers.controller;

import com.manacommunity.offers.domain.enums.BusinessVerificationStatus;
import com.manacommunity.offers.domain.enums.OfferStatus;
import com.manacommunity.offers.dto.BusinessResponseDto;
import com.manacommunity.offers.dto.CommerceAnalyticsDto;
import com.manacommunity.offers.dto.OfferResponseDto;
import com.manacommunity.offers.service.BusinessService;
import com.manacommunity.offers.service.CommerceAnalyticsService;
import com.manacommunity.offers.service.CommunityOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
