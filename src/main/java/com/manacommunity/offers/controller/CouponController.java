package com.manacommunity.offers.controller;

import com.manacommunity.offers.dto.*;
import com.manacommunity.offers.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers/coupons")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<CouponDto> createCoupon(@Valid @RequestBody CreateCouponRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.createCoupon(req));
    }

    @GetMapping
    public ResponseEntity<List<CouponDto>> getCoupons(@RequestParam(required = false) String businessId) {
        return ResponseEntity.ok(couponService.getActiveCoupons(businessId));
    }

    @PostMapping("/validate")
    public ResponseEntity<CouponValidationResponse> validateCoupon(@Valid @RequestBody ValidateCouponRequest req) {
        return ResponseEntity.ok(couponService.validateCoupon(req));
    }

    @PostMapping("/apply")
    public ResponseEntity<CouponValidationResponse> applyCoupon(@Valid @RequestBody ValidateCouponRequest req) {
        return ResponseEntity.ok(couponService.applyCoupon(req));
    }
}
