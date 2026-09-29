package com.manacommunity.offers.entity;

import com.manacommunity.offers.domain.enums.BoothPackageType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "market_booths")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketBoothEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100)
    private String marketEventId;

    @Column(nullable = false, length = 30)
    private String boothNumber; // e.g. "A-01", "B-04"

    @Column(nullable = false, length = 60)
    private String zone; // e.g. "Food Court", "Health & Wellness", "Kids & Games", "Fashion & Lifestyle"

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BoothPackageType packageType = BoothPackageType.BASIC_BOOTH;

    private Double price;

    @Builder.Default
    private Boolean isOccupied = false;

    @Column(length = 100)
    private String assignedBusinessId;

    @Column(length = 150)
    private String assignedBusinessName;

    @Column(length = 100)
    private String assignedCategory;

    @Column(length = 255)
    private String todaysSpecialOffer; // e.g. "Free dental screening + 40% voucher"

    private Integer displayRow; // for visual map grid
    private Integer displayCol; // for visual map grid

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
