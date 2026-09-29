package com.manacommunity.offers.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_sponsorships")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunitySponsorshipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100)
    private String communityId;

    @Column(length = 150)
    private String communityName;

    @Column(nullable = false, length = 100)
    private String businessId;

    @Column(nullable = false, length = 150)
    private String businessName;

    @Column(nullable = false, length = 200)
    private String eventOrTournamentName; // e.g. "EPL Cricket Tournament 2026", "Mana Family Day"

    @Column(nullable = false, length = 100)
    private String sponsorshipTier; // e.g. "Title Sponsor", "Jersey Sponsor", "Trophies Partner", "Refreshment Partner"

    private Double amount;

    @Column(columnDefinition = "TEXT")
    private String deliverables; // e.g. "Main stage branding, jersey logo, 100 sample vouchers"

    @Builder.Default
    private Boolean active = true;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
