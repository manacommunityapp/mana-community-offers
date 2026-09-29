package com.manacommunity.offers.entity;

import com.manacommunity.offers.domain.enums.ClaimStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "offer_claims", indexes = {
    @Index(name = "idx_claims_code", columnList = "redemptionCode", unique = true),
    @Index(name = "idx_claims_user_offer", columnList = "residentUserId, offerId")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferClaimEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100)
    private String offerId;

    @Column(nullable = false, length = 100)
    private String businessId;

    @Column(nullable = false, length = 100)
    private String communityId;

    @Column(nullable = false, length = 100)
    private String residentUserId;

    @Column(length = 150)
    private String residentName;

    @Column(length = 50)
    private String unitNumber; // e.g. "B-402"

    @Column(nullable = false, unique = true, length = 40)
    private String redemptionCode; // e.g. "MANA-8F29K"

    @Column(length = 500)
    private String qrPayload;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ClaimStatus status = ClaimStatus.ACTIVE;

    private LocalDate validUntil;

    private LocalDateTime redeemedAt;

    @Column(length = 100)
    private String redeemedByStaff;

    @Column(length = 255)
    private String redemptionNotes;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime claimedAt;
}
