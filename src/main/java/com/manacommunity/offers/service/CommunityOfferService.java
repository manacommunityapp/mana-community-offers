package com.manacommunity.offers.service;

import com.manacommunity.offers.domain.enums.ClaimStatus;
import com.manacommunity.offers.domain.enums.OfferStatus;
import com.manacommunity.offers.dto.*;
import com.manacommunity.offers.entity.BusinessCategoryEntity;
import com.manacommunity.offers.entity.BusinessEntity;
import com.manacommunity.offers.entity.CommunityOfferEntity;
import com.manacommunity.offers.entity.OfferClaimEntity;
import com.manacommunity.offers.repository.BusinessCategoryRepository;
import com.manacommunity.offers.repository.BusinessRepository;
import com.manacommunity.offers.repository.CommunityOfferRepository;
import com.manacommunity.offers.repository.OfferClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityOfferService {

    private final CommunityOfferRepository offerRepository;
    private final OfferClaimRepository claimRepository;
    private final BusinessRepository businessRepository;
    private final BusinessCategoryRepository categoryRepository;

    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final SecureRandom random = new SecureRandom();

    @Transactional(readOnly = true)
    public List<OfferResponseDto> getOffersForCommunity(String communityId) {
        LocalDate today = LocalDate.now();
        return offerRepository.findActiveOffersForCommunity(communityId, today).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OfferResponseDto> searchOffers(String communityId, String query) {
        LocalDate today = LocalDate.now();
        return offerRepository.searchOffersForCommunity(communityId, today, query).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OfferResponseDto> getOffersByCategory(String categoryId) {
        return offerRepository.findByCategoryIdAndStatus(categoryId, OfferStatus.PUBLISHED).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OfferResponseDto getOfferById(String id) {
        CommunityOfferEntity entity = offerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found: " + id));
        return mapToDto(entity);
    }

    @Transactional
    public OfferResponseDto createOffer(CreateOfferRequest req) {
        BusinessEntity business = businessRepository.findById(req.getBusinessId())
                .orElseThrow(() -> new IllegalArgumentException("Business not found: " + req.getBusinessId()));

        String catName = categoryRepository.findById(req.getCategoryId())
                .map(BusinessCategoryEntity::getName)
                .orElse("General");

        String targetCommIds = req.getTargetCommunityIds() != null
                ? String.join(",", req.getTargetCommunityIds())
                : "";
        String targetCommNames = req.getTargetCommunityNames() != null
                ? String.join(", ", req.getTargetCommunityNames())
                : "";

        Double regular = req.getRegularPrice();
        Double community = req.getCommunityPrice();
        Double discountPct = req.getDiscountPercentage();

        if (regular != null && community != null && regular > 0) {
            discountPct = Math.round(((regular - community) / regular) * 100.0 * 10.0) / 10.0;
        }

        String savings = req.getSavingsSummary();
        if ((savings == null || savings.isBlank()) && regular != null && community != null && regular > community) {
            savings = String.format("Save ₹%.0f (%s%% OFF)", (regular - community), discountPct != null ? discountPct.intValue() : 0);
        }

        CommunityOfferEntity entity = CommunityOfferEntity.builder()
                .businessId(business.getId())
                .businessName(business.getName())
                .businessLogoUrl(business.getLogoUrl())
                .categoryId(req.getCategoryId())
                .categoryName(catName)
                .title(req.getTitle())
                .tagline(req.getTagline())
                .description(req.getDescription())
                .coverImageUrl(req.getCoverImageUrl())
                .dealType(req.getDealType())
                .regularPrice(regular)
                .communityPrice(community)
                .discountPercentage(discountPct)
                .savingsSummary(savings)
                .termsAndConditions(req.getTermsAndConditions())
                .eligibilityNote(req.getEligibilityNote())
                .targetCommunityIds(targetCommIds)
                .targetCommunityNames(targetCommNames)
                .estimatedAudience(req.getEstimatedAudience() != null ? req.getEstimatedAudience() : 1500)
                .validFrom(req.getValidFrom() != null ? req.getValidFrom() : LocalDate.now())
                .validUntil(req.getValidUntil() != null ? req.getValidUntil() : LocalDate.now().plusDays(30))
                .maxClaims(req.getMaxClaims() != null ? req.getMaxClaims() : 100)
                .claimedCount(0)
                .redeemedCount(0)
                .status(OfferStatus.PUBLISHED) // auto-publish for initial verified businesses or set PENDING
                .featured(Boolean.TRUE.equals(req.getFeatured()))
                .viewCount(0)
                .build();

        CommunityOfferEntity saved = offerRepository.save(entity);

        // Update active deals count on business
        business.setActiveDealsCount(business.getActiveDealsCount() + 1);
        businessRepository.save(business);

        log.info("Created community offer: {} for business {}", saved.getTitle(), business.getName());
        return mapToDto(saved);
    }

    @Transactional
    public ClaimResponseDto claimOffer(ClaimOfferRequest req) {
        // Concurrency-safe pessimistic lock
        CommunityOfferEntity offer = offerRepository.findByIdWithPessimisticLock(req.getOfferId())
                .orElseThrow(() -> new IllegalArgumentException("Offer not found: " + req.getOfferId()));

        if (offer.getStatus() != OfferStatus.PUBLISHED) {
            throw new IllegalStateException("Offer is not currently active for claiming");
        }

        if (offer.getValidUntil() != null && offer.getValidUntil().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Offer has expired");
        }

        if (offer.getMaxClaims() != null && offer.getClaimedCount() >= offer.getMaxClaims()) {
            throw new IllegalStateException("Offer claim limit has been reached");
        }

        // Check if resident already has an active claim for this offer
        if (claimRepository.existsByOfferIdAndResidentUserIdAndStatus(offer.getId(), req.getResidentUserId(), ClaimStatus.ACTIVE)) {
            throw new IllegalStateException("You already have an active voucher for this offer");
        }

        // Generate unique code
        String redemptionCode = generateRedemptionCode();
        String qrPayload = String.format("MANADEAL:%s:%s:%s", offer.getId(), req.getResidentUserId(), redemptionCode);

        BusinessEntity business = businessRepository.findById(offer.getBusinessId()).orElse(null);

        OfferClaimEntity claim = OfferClaimEntity.builder()
                .offerId(offer.getId())
                .businessId(offer.getBusinessId())
                .communityId(req.getCommunityId())
                .residentUserId(req.getResidentUserId())
                .residentName(req.getResidentName())
                .unitNumber(req.getUnitNumber())
                .redemptionCode(redemptionCode)
                .qrPayload(qrPayload)
                .status(ClaimStatus.ACTIVE)
                .validUntil(offer.getValidUntil())
                .build();

        OfferClaimEntity savedClaim = claimRepository.save(claim);

        // Increment claim count
        offer.setClaimedCount(offer.getClaimedCount() + 1);
        offerRepository.save(offer);

        log.info("Resident {} claimed offer {} with code {}", req.getResidentUserId(), offer.getTitle(), redemptionCode);

        return ClaimResponseDto.builder()
                .id(savedClaim.getId())
                .offerId(offer.getId())
                .offerTitle(offer.getTitle())
                .dealType(offer.getDealType())
                .regularPrice(offer.getRegularPrice())
                .communityPrice(offer.getCommunityPrice())
                .savingsSummary(offer.getSavingsSummary())
                .businessId(offer.getBusinessId())
                .businessName(offer.getBusinessName())
                .businessLogoUrl(offer.getBusinessLogoUrl())
                .businessAddress(business != null ? business.getAddress() : "")
                .businessPhone(business != null ? business.getPhone() : "")
                .communityId(req.getCommunityId())
                .residentUserId(req.getResidentUserId())
                .residentName(req.getResidentName())
                .unitNumber(req.getUnitNumber())
                .redemptionCode(savedClaim.getRedemptionCode())
                .qrPayload(savedClaim.getQrPayload())
                .status(savedClaim.getStatus())
                .validUntil(savedClaim.getValidUntil())
                .claimedAt(savedClaim.getClaimedAt())
                .build();
    }

    @Transactional
    public ClaimResponseDto redeemOffer(RedeemOfferRequest req) {
        OfferClaimEntity claim = claimRepository.findByRedemptionCode(req.getRedemptionCode().trim().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid redemption code: " + req.getRedemptionCode()));

        if (claim.getStatus() == ClaimStatus.REDEEMED) {
            throw new IllegalStateException("Voucher has already been redeemed on " + claim.getRedeemedAt());
        }

        if (claim.getStatus() == ClaimStatus.EXPIRED || (claim.getValidUntil() != null && claim.getValidUntil().isBefore(LocalDate.now()))) {
            claim.setStatus(ClaimStatus.EXPIRED);
            claimRepository.save(claim);
            throw new IllegalStateException("Voucher has expired");
        }

        if (claim.getStatus() != ClaimStatus.ACTIVE) {
            throw new IllegalStateException("Voucher is not in active state");
        }

        claim.setStatus(ClaimStatus.REDEEMED);
        claim.setRedeemedAt(LocalDateTime.now());
        claim.setRedeemedByStaff(req.getStaffName());
        claim.setRedemptionNotes(req.getNotes());
        OfferClaimEntity savedClaim = claimRepository.save(claim);

        // Update counts on offer & business
        offerRepository.findById(claim.getOfferId()).ifPresent(o -> {
            o.setRedeemedCount(o.getRedeemedCount() + 1);
            offerRepository.save(o);
        });

        businessRepository.findById(claim.getBusinessId()).ifPresent(b -> {
            b.setTotalRedemptions(b.getTotalRedemptions() + 1);
            businessRepository.save(b);
        });

        CommunityOfferEntity offer = offerRepository.findById(claim.getOfferId()).orElse(null);
        BusinessEntity business = businessRepository.findById(claim.getBusinessId()).orElse(null);

        log.info("Redeemed voucher {} by business staff {}", claim.getRedemptionCode(), req.getStaffName());

        return ClaimResponseDto.builder()
                .id(savedClaim.getId())
                .offerId(claim.getOfferId())
                .offerTitle(offer != null ? offer.getTitle() : "")
                .dealType(offer != null ? offer.getDealType() : null)
                .regularPrice(offer != null ? offer.getRegularPrice() : null)
                .communityPrice(offer != null ? offer.getCommunityPrice() : null)
                .savingsSummary(offer != null ? offer.getSavingsSummary() : "")
                .businessId(claim.getBusinessId())
                .businessName(business != null ? business.getName() : "")
                .businessLogoUrl(business != null ? business.getLogoUrl() : "")
                .communityId(claim.getCommunityId())
                .residentUserId(claim.getResidentUserId())
                .residentName(claim.getResidentName())
                .unitNumber(claim.getUnitNumber())
                .redemptionCode(savedClaim.getRedemptionCode())
                .qrPayload(savedClaim.getQrPayload())
                .status(savedClaim.getStatus())
                .validUntil(savedClaim.getValidUntil())
                .claimedAt(savedClaim.getClaimedAt())
                .redeemedAt(savedClaim.getRedeemedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ClaimResponseDto> getResidentClaims(String residentUserId) {
        return claimRepository.findByResidentUserIdOrderByClaimedAtDesc(residentUserId).stream()
                .map(c -> {
                    CommunityOfferEntity offer = offerRepository.findById(c.getOfferId()).orElse(null);
                    BusinessEntity business = businessRepository.findById(c.getBusinessId()).orElse(null);
                    return ClaimResponseDto.builder()
                            .id(c.getId())
                            .offerId(c.getOfferId())
                            .offerTitle(offer != null ? offer.getTitle() : "Community Offer")
                            .dealType(offer != null ? offer.getDealType() : null)
                            .regularPrice(offer != null ? offer.getRegularPrice() : null)
                            .communityPrice(offer != null ? offer.getCommunityPrice() : null)
                            .savingsSummary(offer != null ? offer.getSavingsSummary() : "")
                            .businessId(c.getBusinessId())
                            .businessName(business != null ? business.getName() : "")
                            .businessLogoUrl(business != null ? business.getLogoUrl() : "")
                            .businessAddress(business != null ? business.getAddress() : "")
                            .businessPhone(business != null ? business.getPhone() : "")
                            .communityId(c.getCommunityId())
                            .residentUserId(c.getResidentUserId())
                            .residentName(c.getResidentName())
                            .unitNumber(c.getUnitNumber())
                            .redemptionCode(c.getRedemptionCode())
                            .qrPayload(c.getQrPayload())
                            .status(c.getStatus())
                            .validUntil(c.getValidUntil())
                            .claimedAt(c.getClaimedAt())
                            .redeemedAt(c.getRedeemedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public OfferResponseDto updateOfferStatus(String offerId, OfferStatus status, String adminUserId, String rejectionReason) {
        CommunityOfferEntity offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found: " + offerId));
        offer.setStatus(status);
        offer.setApprovedByUserId(adminUserId);
        offer.setApprovedAt(LocalDateTime.now());
        offer.setRejectionReason(rejectionReason);
        CommunityOfferEntity updated = offerRepository.save(offer);
        return mapToDto(updated);
    }

    private String generateRedemptionCode() {
        StringBuilder sb = new StringBuilder("MANA-");
        for (int i = 0; i < 5; i++) {
            sb.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }

    public OfferResponseDto mapToDto(CommunityOfferEntity entity) {
        List<String> commIds = entity.getTargetCommunityIds() != null && !entity.getTargetCommunityIds().isBlank()
                ? Arrays.asList(entity.getTargetCommunityIds().split(","))
                : Collections.emptyList();

        List<String> commNames = entity.getTargetCommunityNames() != null && !entity.getTargetCommunityNames().isBlank()
                ? Arrays.asList(entity.getTargetCommunityNames().split(",\\s*"))
                : Collections.emptyList();

        int max = entity.getMaxClaims() != null ? entity.getMaxClaims() : 100;
        int claimed = entity.getClaimedCount() != null ? entity.getClaimedCount() : 0;
        int available = Math.max(0, max - claimed);

        return OfferResponseDto.builder()
                .id(entity.getId())
                .businessId(entity.getBusinessId())
                .businessName(entity.getBusinessName())
                .businessLogoUrl(entity.getBusinessLogoUrl())
                .categoryId(entity.getCategoryId())
                .categoryName(entity.getCategoryName())
                .title(entity.getTitle())
                .tagline(entity.getTagline())
                .description(entity.getDescription())
                .coverImageUrl(entity.getCoverImageUrl())
                .dealType(entity.getDealType())
                .regularPrice(entity.getRegularPrice())
                .communityPrice(entity.getCommunityPrice())
                .discountPercentage(entity.getDiscountPercentage())
                .savingsSummary(entity.getSavingsSummary())
                .termsAndConditions(entity.getTermsAndConditions())
                .eligibilityNote(entity.getEligibilityNote())
                .targetCommunityIds(commIds)
                .targetCommunityNames(commNames)
                .estimatedAudience(entity.getEstimatedAudience())
                .validFrom(entity.getValidFrom())
                .validUntil(entity.getValidUntil())
                .maxClaims(max)
                .claimedCount(claimed)
                .redeemedCount(entity.getRedeemedCount() != null ? entity.getRedeemedCount() : 0)
                .availableClaims(available)
                .isClaimable(available > 0 && entity.getStatus() == OfferStatus.PUBLISHED)
                .status(entity.getStatus())
                .featured(entity.getFeatured())
                .viewCount(entity.getViewCount())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
