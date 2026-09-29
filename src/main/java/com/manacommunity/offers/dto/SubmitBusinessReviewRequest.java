package com.manacommunity.offers.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitBusinessReviewRequest {

    @NotBlank(message = "Business ID is required")
    private String businessId;

    private String offerId;

    @NotBlank(message = "Resident user ID is required")
    private String residentUserId;

    private String residentName;
    private String unitNumber;

    @NotNull(message = "Rating is required")
    @Min(1)
    @Max(5)
    private Integer rating;

    private String comment;
}
