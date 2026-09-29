package com.manacommunity.offers.entity;

import com.manacommunity.offers.domain.enums.DemandStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_demands")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityDemandEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100)
    private String communityId;

    @Column(length = 150)
    private String communityName;

    @Column(nullable = false, length = 200)
    private String title; // e.g. "Weekend swimming classes for kids"

    @Column(nullable = false, length = 100)
    private String categoryId;

    @Column(length = 100)
    private String categoryName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String expectedFrequency; // e.g. "Weekly on Sundays", "One-time camp", "Monthly"

    @Column(length = 100)
    private String preferredTiming; // e.g. "Mornings 7 AM - 9 AM"

    @Builder.Default
    private Integer interestedFamiliesCount = 1;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DemandStatus status = DemandStatus.OPEN;

    @Column(length = 100)
    private String createdByUserId;

    @Column(length = 100)
    private String createdByName;

    @Column(length = 100)
    private String fulfilledByBusinessId;

    @Column(length = 150)
    private String fulfilledByBusinessName;

    @Column(length = 100)
    private String resultingOfferId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
