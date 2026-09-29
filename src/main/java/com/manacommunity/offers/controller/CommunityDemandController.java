package com.manacommunity.offers.controller;

import com.manacommunity.offers.dto.CreateDemandRequest;
import com.manacommunity.offers.dto.DemandResponseDto;
import com.manacommunity.offers.service.CommunityDemandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers/demands")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommunityDemandController {

    private final CommunityDemandService demandService;

    @GetMapping
    public ResponseEntity<List<DemandResponseDto>> getDemands(
            @RequestParam(defaultValue = "comm-mana-residency") String communityId,
            @RequestParam(required = false) String userId) {
        return ResponseEntity.ok(demandService.getDemandsForCommunity(communityId, userId));
    }

    @PostMapping
    public ResponseEntity<DemandResponseDto> createDemand(@Valid @RequestBody CreateDemandRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(demandService.createDemand(req));
    }

    @PostMapping("/{id}/interest")
    public ResponseEntity<DemandResponseDto> toggleInterest(
            @PathVariable String id,
            @RequestParam String residentUserId,
            @RequestParam(required = false) String residentName,
            @RequestParam(required = false) String flatNumber) {
        return ResponseEntity.ok(demandService.toggleInterest(id, residentUserId, residentName, flatNumber));
    }
}
