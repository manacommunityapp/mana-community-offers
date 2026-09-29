package com.manacommunity.offers.dto;

import com.manacommunity.offers.domain.enums.BoothPackageType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketBoothResponseDto {

    private String id;
    private String marketEventId;
    private String boothNumber;
    private String zone;
    private BoothPackageType packageType;
    private Double price;
    private Boolean isOccupied;
    private String assignedBusinessId;
    private String assignedBusinessName;
    private String assignedCategory;
    private String todaysSpecialOffer;
    private Integer displayRow;
    private Integer displayCol;
}
