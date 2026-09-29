package com.manacommunity.offers.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "market_vendor_passes")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketVendorPassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100)
    private String marketEventId;

    @Column(nullable = false, length = 100)
    private String businessId;

    @Column(nullable = false, length = 150)
    private String businessName;

    @Column(nullable = false, length = 30)
    private String boothNumber;

    @Column(nullable = false, length = 100)
    private String staffName;

    @Column(length = 20)
    private String staffPhone;

    @Column(length = 30)
    private String vehicleNumber;

    @Column(nullable = false, unique = true, length = 50)
    private String passToken; // e.g. "VPASS-EVT-9021"

    @Column(length = 500)
    private String qrCodePayload;

    private LocalDate validDate;

    @Builder.Default
    private Boolean checkedIn = false;

    private LocalDateTime checkedInAt;

    @Column(length = 100)
    private String checkedInByGuard;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
