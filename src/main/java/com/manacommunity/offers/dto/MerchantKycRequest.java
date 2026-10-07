package com.manacommunity.offers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantKycRequest {

    private String registeredEntityName;

    @NotBlank(message = "GSTIN or Tax ID is required")
    private String gstin;

    private String businessRegistrationNumber;

    private String kycDocumentUrl;

    @NotBlank(message = "Bank Account Number is required")
    private String bankAccountNumber;

    @NotBlank(message = "Bank IFSC Code is required")
    private String bankIfscCode;

    @NotBlank(message = "Bank Account Holder Name is required")
    private String bankAccountHolder;

    private String bankName;
}
