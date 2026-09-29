package com.manacommunity.offers.entity;

import com.manacommunity.offers.domain.enums.MarketEventStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "community_market_events")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityMarketEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100)
    private String communityId;

    @Column(nullable = false, length = 150)
    private String communityName;

    @Column(nullable = false, length = 200)
    private String title; // e.g. "Mana Family Shopping Fest & Market Day"

    @Column(length = 255)
    private String theme; // e.g. "Diwali Bazaar & Food Carnival"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String bannerImageUrl;

    private LocalDate eventDate;

    @Column(length = 20)
    private String startTime; // e.g. "16:00"

    @Column(length = 20)
    private String endTime; // e.g. "21:00"

    @Column(length = 150)
    private String venue; // e.g. "Clubhouse Central Lawn & Amphitheatre"

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MarketEventStatus status = MarketEventStatus.UPCOMING;

    @Builder.Default
    private Integer totalBooths = 20;

    @Builder.Default
    private Integer allocatedBooths = 0;

    @Builder.Default
    private Integer expectedVisitors = 1500;

    @Column(columnDefinition = "TEXT")
    private String eventGuidelines; // noise, electric safety, hygiene rules

    @Column(columnDefinition = "TEXT")
    private String entertainmentHighlights; // DJ, Kids Games, Magic Show

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
