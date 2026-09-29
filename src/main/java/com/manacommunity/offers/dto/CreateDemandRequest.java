package com.manacommunity.offers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDemandRequest {

    @NotBlank(message = "Community ID is required")
    private String communityId;

    private String communityName;

    @NotBlank(message = "Demand title is required")
    private String title;

    @NotBlank(message = "Category ID is required")
    private String categoryId;

    private String description;
    private String expectedFrequency;
    private String preferredTiming;
    private String createdByUserId;
    private String createdByName;
}
