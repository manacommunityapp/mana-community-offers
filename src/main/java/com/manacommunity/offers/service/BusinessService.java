package com.manacommunity.offers.service;

import com.manacommunity.offers.domain.enums.BusinessVerificationStatus;
import com.manacommunity.offers.dto.BusinessResponseDto;
import com.manacommunity.offers.dto.RegisterBusinessRequest;
import com.manacommunity.offers.entity.BusinessCategoryEntity;
import com.manacommunity.offers.entity.BusinessEntity;
import com.manacommunity.offers.entity.CommunityBusinessEntity;
import com.manacommunity.offers.repository.BusinessCategoryRepository;
import com.manacommunity.offers.repository.BusinessRepository;
import com.manacommunity.offers.repository.CommunityBusinessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final BusinessCategoryRepository categoryRepository;
    private final CommunityBusinessRepository communityBusinessRepository;

    @Transactional(readOnly = true)
    public List<BusinessResponseDto> getAllBusinesses() {
        return businessRepository.findByActiveTrueOrderByAverageRatingDesc().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BusinessResponseDto getBusinessById(String id) {
        BusinessEntity entity = businessRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Business not found: " + id));
        return mapToDto(entity);
    }

    @Transactional(readOnly = true)
    public List<BusinessResponseDto> getBusinessesByCategory(String categoryId) {
        return businessRepository.findByCategoryIdAndActiveTrue(categoryId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BusinessResponseDto> searchBusinesses(String query) {
        return businessRepository.searchBusinesses(query).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BusinessResponseDto registerBusiness(RegisterBusinessRequest req) {
        String categoryName = "General";
        if (req.getCategoryId() != null) {
            categoryName = categoryRepository.findById(req.getCategoryId())
                    .map(BusinessCategoryEntity::getName)
                    .orElse("General");
        }

        BusinessEntity entity = BusinessEntity.builder()
                .name(req.getName())
                .registeredEntityName(req.getRegisteredEntityName())
                .categoryId(req.getCategoryId())
                .categoryName(categoryName)
                .description(req.getDescription())
                .tagline(req.getTagline())
                .logoUrl(req.getLogoUrl())
                .bannerUrl(req.getBannerUrl())
                .address(req.getAddress())
                .city(req.getCity())
                .pincode(req.getPincode())
                .phone(req.getPhone())
                .email(req.getEmail())
                .websiteUrl(req.getWebsiteUrl())
                .googleMapsUrl(req.getGoogleMapsUrl())
                .distanceKm(req.getDistanceKm() != null ? req.getDistanceKm() : 1.2)
                .ownerUserId(req.getOwnerUserId())
                .verificationStatus(BusinessVerificationStatus.PENDING)
                .build();

        BusinessEntity saved = businessRepository.save(entity);
        log.info("Registered new business: {} ({})", saved.getName(), saved.getId());
        return mapToDto(saved);
    }

    @Transactional
    public BusinessResponseDto submitKyc(String businessId, com.manacommunity.offers.dto.MerchantKycRequest req) {
        BusinessEntity entity = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found: " + businessId));

        if (req.getRegisteredEntityName() != null) {
            entity.setRegisteredEntityName(req.getRegisteredEntityName());
        }
        entity.setGstin(req.getGstin());
        entity.setBusinessRegistrationNumber(req.getBusinessRegistrationNumber());
        entity.setKycDocumentUrl(req.getKycDocumentUrl());
        entity.setBankAccountNumber(req.getBankAccountNumber());
        entity.setBankIfscCode(req.getBankIfscCode());
        entity.setBankAccountHolder(req.getBankAccountHolder());
        entity.setBankName(req.getBankName());
        entity.setVerificationStatus(BusinessVerificationStatus.PENDING);

        BusinessEntity saved = businessRepository.save(entity);
        log.info("Submitted KYC for business {}: GSTIN {}", businessId, req.getGstin());
        return mapToDto(saved);
    }

    @Transactional
    public BusinessResponseDto verifyMerchant(String businessId, com.manacommunity.offers.dto.VerifyMerchantRequest req) {
        BusinessEntity entity = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found: " + businessId));

        entity.setVerificationStatus(req.getStatus());
        if (req.getPartnershipTier() != null) {
            entity.setPartnershipTier(req.getPartnershipTier());
        }
        if (req.getCommissionRatePct() != null) {
            entity.setCommissionRatePct(req.getCommissionRatePct());
        }
        entity.setVerifiedByUserId(req.getAdminUserId() != null ? req.getAdminUserId() : "admin");
        entity.setVerifiedAt(LocalDateTime.now());
        entity.setRejectionReason(req.getRejectionReason());

        BusinessEntity updated = businessRepository.save(entity);
        log.info("Merchant {} verification updated to {} by admin {}", businessId, req.getStatus(), req.getAdminUserId());
        return mapToDto(updated);
    }

    @Transactional(readOnly = true)
    public List<BusinessResponseDto> getPendingVerificationBusinesses() {
        return businessRepository.findAll().stream()
                .filter(b -> b.getVerificationStatus() == BusinessVerificationStatus.PENDING)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BusinessResponseDto updateVerificationStatus(String businessId, BusinessVerificationStatus status, String adminUserId) {
        BusinessEntity entity = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found: " + businessId));

        entity.setVerificationStatus(status);
        entity.setVerifiedByUserId(adminUserId);
        entity.setVerifiedAt(LocalDateTime.now());
        BusinessEntity updated = businessRepository.save(entity);
        log.info("Updated business {} status to {}", businessId, status);
        return mapToDto(updated);
    }

    @Transactional
    public void linkBusinessToCommunity(String businessId, String communityId, String communityName) {
        if (communityBusinessRepository.findByBusinessIdAndCommunityId(businessId, communityId).isEmpty()) {
            CommunityBusinessEntity cb = CommunityBusinessEntity.builder()
                    .businessId(businessId)
                    .communityId(communityId)
                    .communityName(communityName)
                    .status(BusinessVerificationStatus.VERIFIED)
                    .approvedAt(LocalDateTime.now())
                    .build();
            communityBusinessRepository.save(cb);
        }
    }

    public BusinessResponseDto mapToDto(BusinessEntity entity) {
        return BusinessResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .registeredEntityName(entity.getRegisteredEntityName())
                .categoryId(entity.getCategoryId())
                .categoryName(entity.getCategoryName())
                .description(entity.getDescription())
                .tagline(entity.getTagline())
                .logoUrl(entity.getLogoUrl())
                .bannerUrl(entity.getBannerUrl())
                .address(entity.getAddress())
                .city(entity.getCity())
                .pincode(entity.getPincode())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .websiteUrl(entity.getWebsiteUrl())
                .googleMapsUrl(entity.getGoogleMapsUrl())
                .distanceKm(entity.getDistanceKm())
                .verificationStatus(entity.getVerificationStatus())
                .partnershipTier(entity.getPartnershipTier())
                .averageRating(entity.getAverageRating())
                .reviewCount(entity.getReviewCount())
                .activeDealsCount(entity.getActiveDealsCount())
                .totalRedemptions(entity.getTotalRedemptions())
                .ownerUserId(entity.getOwnerUserId())
                .gstin(entity.getGstin())
                .businessRegistrationNumber(entity.getBusinessRegistrationNumber())
                .kycDocumentUrl(entity.getKycDocumentUrl())
                .bankAccountNumber(entity.getBankAccountNumber())
                .bankIfscCode(entity.getBankIfscCode())
                .bankAccountHolder(entity.getBankAccountHolder())
                .bankName(entity.getBankName())
                .commissionRatePct(entity.getCommissionRatePct())
                .verifiedByUserId(entity.getVerifiedByUserId())
                .verifiedAt(entity.getVerifiedAt())
                .rejectionReason(entity.getRejectionReason())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
