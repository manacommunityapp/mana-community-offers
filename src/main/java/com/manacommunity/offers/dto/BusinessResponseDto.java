package com.manacommunity.offers.dto;

import com.manacommunity.offers.domain.enums.BusinessVerificationStatus;
import com.manacommunity.offers.domain.enums.PartnershipTier;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessResponseDto {

    private String id;
    private String name;
    private String registeredEntityName;
    private String categoryId;
    private String categoryName;
    private String description;
    private String tagline;
    private String logoUrl;
    private String bannerUrl;
    private String address;
    private String city;
    private String pincode;
    private String phone;
    private String email;
    private String websiteUrl;
    private String googleMapsUrl;
    private Double distanceKm;
    private BusinessVerificationStatus verificationStatus;
    private PartnershipTier partnershipTier;
    private Double averageRating;
    private Integer reviewCount;
    private Integer activeDealsCount;
    private Integer totalRedemptions;
    private String ownerUserId;
    private LocalDateTime createdAt;
}
