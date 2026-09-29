package com.manacommunity.offers.dto;

import com.manacommunity.offers.domain.enums.BoothPackageType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplyMarketBoothRequest {

    @NotBlank(message = "Market event ID is required")
    private String marketEventId;

    @NotBlank(message = "Business ID is required")
    private String businessId;

    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private BoothPackageType requestedPackage;
    private String preferredZone;
    private String productsOrServices;
    private String todaysSpecialOffer;
    private Boolean electricityRequired;
    private Integer tablesRequired;
    private Integer staffPassesRequired;
}
