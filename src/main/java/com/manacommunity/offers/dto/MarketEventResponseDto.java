package com.manacommunity.offers.dto;

import com.manacommunity.offers.domain.enums.MarketEventStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketEventResponseDto {

    private String id;
    private String communityId;
    private String communityName;
    private String title;
    private String theme;
    private String description;
    private String bannerImageUrl;
    private LocalDate eventDate;
    private String startTime;
    private String endTime;
    private String venue;
    private MarketEventStatus status;
    private Integer totalBooths;
    private Integer allocatedBooths;
    private Integer availableBooths;
    private Integer expectedVisitors;
    private String eventGuidelines;
    private String entertainmentHighlights;
    private List<MarketBoothResponseDto> booths;
    private LocalDateTime createdAt;
}
