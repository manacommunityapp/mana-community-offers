package com.manacommunity.offers.controller;

import com.manacommunity.offers.dto.*;
import com.manacommunity.offers.service.CommunityOfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers/deals")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommunityOfferController {

    private final CommunityOfferService offerService;

    @GetMapping
    public ResponseEntity<List<OfferResponseDto>> getOffers(
            @RequestParam(defaultValue = "comm-mana-residency") String communityId,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String search) {

        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(offerService.searchOffers(communityId, search.trim()));
        }
        if (categoryId != null && !categoryId.isBlank()) {
            return ResponseEntity.ok(offerService.getOffersByCategory(categoryId));
        }
        return ResponseEntity.ok(offerService.getOffersForCommunity(communityId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferResponseDto> getOfferById(@PathVariable String id) {
        return ResponseEntity.ok(offerService.getOfferById(id));
    }

    @PostMapping
    public ResponseEntity<OfferResponseDto> createOffer(@Valid @RequestBody CreateOfferRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(offerService.createOffer(req));
    }

    @PostMapping("/claim")
    public ResponseEntity<ClaimResponseDto> claimOffer(@Valid @RequestBody ClaimOfferRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(offerService.claimOffer(req));
    }

    @PostMapping("/redeem")
    public ResponseEntity<ClaimResponseDto> redeemOffer(@Valid @RequestBody RedeemOfferRequest req) {
        return ResponseEntity.ok(offerService.redeemOffer(req));
    }

    @GetMapping("/my-claims")
    public ResponseEntity<List<ClaimResponseDto>> getMyClaims(
            @RequestParam(defaultValue = "user-sandeep") String residentUserId) {
        return ResponseEntity.ok(offerService.getResidentClaims(residentUserId));
    }
}
