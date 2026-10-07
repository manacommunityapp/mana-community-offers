package com.manacommunity.offers.service;

import com.manacommunity.offers.domain.enums.CouponDiscountType;
import com.manacommunity.offers.domain.enums.CouponStatus;
import com.manacommunity.offers.dto.*;
import com.manacommunity.offers.entity.BusinessEntity;
import com.manacommunity.offers.entity.CouponEntity;
import com.manacommunity.offers.entity.CouponUsageEntity;
import com.manacommunity.offers.repository.BusinessRepository;
import com.manacommunity.offers.repository.CouponRepository;
import com.manacommunity.offers.repository.CouponUsageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository usageRepository;
    private final BusinessRepository businessRepository;

    @Transactional
    public CouponDto createCoupon(CreateCouponRequest req) {
        String bizName = null;
        if (req.getBusinessId() != null && !req.getBusinessId().isBlank()) {
            bizName = businessRepository.findById(req.getBusinessId())
                    .map(BusinessEntity::getName)
                    .orElse("Community Partner");
        }

        CouponEntity coupon = CouponEntity.builder()
                .code(req.getCode().trim().toUpperCase())
                .title(req.getTitle())
                .description(req.getDescription())
                .businessId(req.getBusinessId())
                .businessName(bizName)
                .communityId(req.getCommunityId() != null ? req.getCommunityId() : "comm-mana-residency")
                .discountType(req.getDiscountType())
                .discountValue(req.getDiscountValue())
                .minOrderAmount(req.getMinOrderAmount())
                .maxDiscountAmount(req.getMaxDiscountAmount())
                .validFrom(req.getValidFrom() != null ? req.getValidFrom() : LocalDate.now())
                .validUntil(req.getValidUntil() != null ? req.getValidUntil() : LocalDate.now().plusDays(30))
                .usageLimitTotal(req.getUsageLimitTotal() != null ? req.getUsageLimitTotal() : 500)
                .usageLimitPerUser(req.getUsageLimitPerUser() != null ? req.getUsageLimitPerUser() : 1)
                .totalUsedCount(0)
                .status(CouponStatus.ACTIVE)
                .active(true)
                .createdByUserId(req.getCreatedByUserId())
                .build();

        CouponEntity saved = couponRepository.save(coupon);
        log.info("Created coupon {} for business {}", saved.getCode(), saved.getBusinessName());
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<CouponDto> getActiveCoupons(String businessId) {
        if (businessId != null && !businessId.isBlank()) {
            return couponRepository.findByBusinessIdAndActiveTrue(businessId).stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        }
        return couponRepository.findByActiveTrueOrderByCreatedAtDesc().stream()
                .filter(c -> c.getStatus() == CouponStatus.ACTIVE)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CouponValidationResponse validateCoupon(ValidateCouponRequest req) {
        String cleanCode = req.getCode().trim().toUpperCase();
        CouponEntity coupon = couponRepository.findByCodeIgnoreCaseAndActiveTrue(cleanCode)
                .orElse(null);

        if (coupon == null) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .code(cleanCode)
                    .message("Coupon code not found or inactive")
                    .originalAmount(req.getOrderAmount())
                    .calculatedDiscount(0.0)
                    .finalPayableAmount(req.getOrderAmount())
                    .build();
        }

        LocalDate today = LocalDate.now();
        if (coupon.getValidFrom() != null && today.isBefore(coupon.getValidFrom())) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .code(cleanCode)
                    .message("Coupon is not yet active")
                    .originalAmount(req.getOrderAmount())
                    .calculatedDiscount(0.0)
                    .finalPayableAmount(req.getOrderAmount())
                    .build();
        }

        if (coupon.getValidUntil() != null && today.isAfter(coupon.getValidUntil())) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .code(cleanCode)
                    .message("Coupon has expired on " + coupon.getValidUntil())
                    .originalAmount(req.getOrderAmount())
                    .calculatedDiscount(0.0)
                    .finalPayableAmount(req.getOrderAmount())
                    .build();
        }

        if (coupon.getUsageLimitTotal() != null && coupon.getTotalUsedCount() >= coupon.getUsageLimitTotal()) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .code(cleanCode)
                    .message("Coupon global redemption limit reached")
                    .originalAmount(req.getOrderAmount())
                    .calculatedDiscount(0.0)
                    .finalPayableAmount(req.getOrderAmount())
                    .build();
        }

        // Per-user limit
        if (req.getResidentUserId() != null && coupon.getUsageLimitPerUser() != null) {
            long usedByUser = usageRepository.countByCouponIdAndResidentUserId(coupon.getId(), req.getResidentUserId());
            if (usedByUser >= coupon.getUsageLimitPerUser()) {
                return CouponValidationResponse.builder()
                        .valid(false)
                        .code(cleanCode)
                        .message("You have reached the maximum allowed uses (" + coupon.getUsageLimitPerUser() + ") for this coupon")
                        .originalAmount(req.getOrderAmount())
                        .calculatedDiscount(0.0)
                        .finalPayableAmount(req.getOrderAmount())
                        .build();
            }
        }

        // Minimum order check
        if (coupon.getMinOrderAmount() != null && req.getOrderAmount() < coupon.getMinOrderAmount()) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .code(cleanCode)
                    .message("Minimum order of ₹" + coupon.getMinOrderAmount() + " required to use this coupon")
                    .originalAmount(req.getOrderAmount())
                    .calculatedDiscount(0.0)
                    .finalPayableAmount(req.getOrderAmount())
                    .minOrderAmount(coupon.getMinOrderAmount())
                    .build();
        }

        // Calculate discount
        double discount = 0.0;
        if (coupon.getDiscountType() == CouponDiscountType.PERCENTAGE) {
            discount = (req.getOrderAmount() * coupon.getDiscountValue()) / 100.0;
            if (coupon.getMaxDiscountAmount() != null && discount > coupon.getMaxDiscountAmount()) {
                discount = coupon.getMaxDiscountAmount();
            }
        } else if (coupon.getDiscountType() == CouponDiscountType.FLAT_AMOUNT) {
            discount = Math.min(req.getOrderAmount(), coupon.getDiscountValue());
        } else if (coupon.getDiscountType() == CouponDiscountType.FREE_SERVICE) {
            discount = req.getOrderAmount();
        }

        discount = Math.round(discount * 100.0) / 100.0;
        double payable = Math.max(0.0, req.getOrderAmount() - discount);

        return CouponValidationResponse.builder()
                .valid(true)
                .couponId(coupon.getId())
                .code(coupon.getCode())
                .message("Coupon applied successfully!")
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .originalAmount(req.getOrderAmount())
                .calculatedDiscount(discount)
                .finalPayableAmount(payable)
                .minOrderAmount(coupon.getMinOrderAmount())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .build();
    }

    @Transactional
    public CouponValidationResponse applyCoupon(ValidateCouponRequest req) {
        CouponValidationResponse val = validateCoupon(req);
        if (!val.isValid()) {
            throw new IllegalStateException(val.getMessage());
        }

        CouponEntity coupon = couponRepository.findById(val.getCouponId())
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));

        // Record usage
        CouponUsageEntity usage = CouponUsageEntity.builder()
                .couponId(coupon.getId())
                .couponCode(coupon.getCode())
                .residentUserId(req.getResidentUserId())
                .businessId(req.getBusinessId() != null ? req.getBusinessId() : coupon.getBusinessId())
                .claimId(req.getClaimId())
                .orderAmount(req.getOrderAmount())
                .discountApplied(val.getCalculatedDiscount())
                .build();
        usageRepository.save(usage);

        // Update counts
        coupon.setTotalUsedCount(coupon.getTotalUsedCount() + 1);
        if (coupon.getUsageLimitTotal() != null && coupon.getTotalUsedCount() >= coupon.getUsageLimitTotal()) {
            coupon.setStatus(CouponStatus.EXHAUSTED);
        }
        couponRepository.save(coupon);

        log.info("Applied coupon {} for resident {}, saved ₹{}", coupon.getCode(), req.getResidentUserId(), val.getCalculatedDiscount());
        return val;
    }

    @Transactional
    public int expireOutdatedCoupons() {
        LocalDate today = LocalDate.now();
        List<CouponEntity> expired = couponRepository.findByStatusAndValidUntilBefore(CouponStatus.ACTIVE, today);
        for (CouponEntity c : expired) {
            c.setStatus(CouponStatus.EXPIRED);
        }
        couponRepository.saveAll(expired);
        log.info("Expired {} outdated coupons", expired.size());
        return expired.size();
    }

    public CouponDto mapToDto(CouponEntity entity) {
        return CouponDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .businessId(entity.getBusinessId())
                .businessName(entity.getBusinessName())
                .communityId(entity.getCommunityId())
                .discountType(entity.getDiscountType())
                .discountValue(entity.getDiscountValue())
                .minOrderAmount(entity.getMinOrderAmount())
                .maxDiscountAmount(entity.getMaxDiscountAmount())
                .validFrom(entity.getValidFrom())
                .validUntil(entity.getValidUntil())
                .usageLimitTotal(entity.getUsageLimitTotal())
                .usageLimitPerUser(entity.getUsageLimitPerUser())
                .totalUsedCount(entity.getTotalUsedCount())
                .status(entity.getStatus())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
