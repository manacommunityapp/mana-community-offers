package com.manacommunity.offers.dto;

import com.manacommunity.offers.domain.enums.DemandStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandResponseDto {

    private String id;
    private String communityId;
    private String communityName;
    private String title;
    private String categoryId;
    private String categoryName;
    private String description;
    private String expectedFrequency;
    private String preferredTiming;
    private Integer interestedFamiliesCount;
    private Boolean userHasExpressedInterest;
    private DemandStatus status;
    private String createdByName;
    private String fulfilledByBusinessName;
    private String resultingOfferId;
    private LocalDateTime createdAt;
}
