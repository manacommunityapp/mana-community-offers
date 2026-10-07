package com.manacommunity.offers.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateSettlementRequest {

    private String businessId; // optional: if null, generate for all businesses with pending commissions
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String adminUserId;
    private String notes;
}
