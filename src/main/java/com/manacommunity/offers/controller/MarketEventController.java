package com.manacommunity.offers.controller;

import com.manacommunity.offers.dto.*;
import com.manacommunity.offers.entity.MarketVendorApplicationEntity;
import com.manacommunity.offers.service.MarketEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers/market-events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MarketEventController {

    private final MarketEventService marketEventService;

    @GetMapping
    public ResponseEntity<List<MarketEventResponseDto>> getMarketEvents(
            @RequestParam(defaultValue = "comm-mana-residency") String communityId) {
        return ResponseEntity.ok(marketEventService.getUpcomingEvents(communityId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarketEventResponseDto> getMarketEventById(@PathVariable String id) {
        return ResponseEntity.ok(marketEventService.getEventById(id));
    }

    @PostMapping
    public ResponseEntity<MarketEventResponseDto> createMarketEvent(@Valid @RequestBody CreateMarketEventRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(marketEventService.createMarketEvent(req));
    }

    @GetMapping("/{id}/booths")
    public ResponseEntity<List<MarketBoothResponseDto>> getBooths(@PathVariable String id) {
        return ResponseEntity.ok(marketEventService.getBoothsForEvent(id));
    }

    @PostMapping("/apply-booth")
    public ResponseEntity<MarketVendorApplicationEntity> applyForBooth(@Valid @RequestBody ApplyMarketBoothRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(marketEventService.applyForBooth(req));
    }

    @PostMapping("/assign-booth")
    public ResponseEntity<MarketBoothResponseDto> assignBooth(
            @RequestParam String boothId,
            @RequestParam String businessId,
            @RequestParam(required = false) String todaysSpecialOffer) {
        return ResponseEntity.ok(marketEventService.assignBooth(boothId, businessId, todaysSpecialOffer));
    }
}
