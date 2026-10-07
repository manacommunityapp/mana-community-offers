package com.manacommunity.offers.controller;

import com.manacommunity.offers.dto.BusinessResponseDto;
import com.manacommunity.offers.dto.RegisterBusinessRequest;
import com.manacommunity.offers.service.BusinessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers/businesses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BusinessController {

    private final BusinessService businessService;
    private final com.manacommunity.offers.service.CommerceAnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<List<BusinessResponseDto>> getAllBusinesses(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(businessService.searchBusinesses(search.trim()));
        }
        if (categoryId != null && !categoryId.isBlank()) {
            return ResponseEntity.ok(businessService.getBusinessesByCategory(categoryId));
        }
        return ResponseEntity.ok(businessService.getAllBusinesses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessResponseDto> getBusinessById(@PathVariable String id) {
        return ResponseEntity.ok(businessService.getBusinessById(id));
    }

    @PostMapping
    public ResponseEntity<BusinessResponseDto> registerBusiness(@Valid @RequestBody RegisterBusinessRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(businessService.registerBusiness(req));
    }

    @PostMapping("/{id}/kyc")
    public ResponseEntity<BusinessResponseDto> submitKyc(
            @PathVariable String id,
            @Valid @RequestBody com.manacommunity.offers.dto.MerchantKycRequest req) {
        return ResponseEntity.ok(businessService.submitKyc(id, req));
    }

    @GetMapping("/{id}/campaign-analytics")
    public ResponseEntity<List<com.manacommunity.offers.dto.CampaignAnalyticsDto>> getBusinessCampaignAnalytics(
            @PathVariable String id) {
        return ResponseEntity.ok(analyticsService.getBusinessCampaignAnalytics(id));
    }
}
