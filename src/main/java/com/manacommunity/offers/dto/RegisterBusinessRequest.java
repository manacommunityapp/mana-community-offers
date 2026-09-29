package com.manacommunity.offers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterBusinessRequest {

    @NotBlank(message = "Business name is required")
    private String name;

    private String registeredEntityName;

    @NotBlank(message = "Category ID is required")
    private String categoryId;

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
    private String ownerUserId;
}
