package com.manacommunity.offers.controller;

import com.manacommunity.offers.dto.*;
import com.manacommunity.offers.service.CommissionSettlementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers/settlements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SettlementController {

    private final CommissionSettlementService settlementService;

    @PostMapping("/generate")
    public ResponseEntity<List<SettlementBatchDto>> generateSettlements(@RequestBody(required = false) GenerateSettlementRequest req) {
        GenerateSettlementRequest request = req != null ? req : GenerateSettlementRequest.builder().build();
        return ResponseEntity.status(HttpStatus.CREATED).body(settlementService.generateSettlements(request));
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<SettlementBatchDto> processSettlement(
            @PathVariable String id,
            @Valid @RequestBody ProcessSettlementRequest req) {
        return ResponseEntity.ok(settlementService.processSettlement(id, req));
    }

    @GetMapping
    public ResponseEntity<List<SettlementBatchDto>> getAllSettlements() {
        return ResponseEntity.ok(settlementService.getAllSettlements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SettlementBatchDto> getSettlementById(@PathVariable String id) {
        return ResponseEntity.ok(settlementService.getSettlementById(id));
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<SettlementBatchDto>> getSettlementsForBusiness(@PathVariable String businessId) {
        return ResponseEntity.ok(settlementService.getSettlementsForBusiness(businessId));
    }

    @GetMapping("/commissions")
    public ResponseEntity<List<CommissionRecordDto>> getAllCommissions() {
        return ResponseEntity.ok(settlementService.getAllCommissions());
    }

    @GetMapping("/commissions/business/{businessId}")
    public ResponseEntity<List<CommissionRecordDto>> getCommissionsForBusiness(@PathVariable String businessId) {
        return ResponseEntity.ok(settlementService.getCommissionsForBusiness(businessId));
    }
}
