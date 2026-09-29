package com.manacommunity.offers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMarketEventRequest {

    @NotBlank(message = "Community ID is required")
    private String communityId;

    private String communityName;

    @NotBlank(message = "Event title is required")
    private String title;

    private String theme;
    private String description;
    private String bannerImageUrl;

    @NotNull(message = "Event date is required")
    private LocalDate eventDate;

    private String startTime;
    private String endTime;
    private String venue;
    private Integer totalBooths;
    private Integer expectedVisitors;
    private String eventGuidelines;
    private String entertainmentHighlights;
}
