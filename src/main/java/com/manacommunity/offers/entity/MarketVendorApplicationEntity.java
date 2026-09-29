package com.manacommunity.offers.entity;

import com.manacommunity.offers.domain.enums.BoothPackageType;
import com.manacommunity.offers.domain.enums.VendorApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "market_vendor_applications")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketVendorApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100)
    private String marketEventId;

    @Column(nullable = false, length = 100)
    private String businessId;

    @Column(nullable = false, length = 150)
    private String businessName;

    @Column(length = 100)
    private String contactPerson;

    @Column(length = 20)
    private String contactPhone;

    @Column(length = 120)
    private String contactEmail;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BoothPackageType requestedPackage = BoothPackageType.BASIC_BOOTH;

    @Column(length = 100)
    private String preferredZone;

    @Column(columnDefinition = "TEXT")
    private String productsOrServices;

    @Column(length = 255)
    private String todaysSpecialOffer;

    @Builder.Default
    private Boolean electricityRequired = false;

    @Builder.Default
    private Integer tablesRequired = 1;

    @Builder.Default
    private Integer staffPassesRequired = 2;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VendorApplicationStatus status = VendorApplicationStatus.PENDING;

    @Column(length = 100)
    private String assignedBoothId;

    @Column(length = 30)
    private String assignedBoothNumber;

    private Double feeAmount;

    @Builder.Default
    private Boolean feePaid = false;

    @Column(length = 255)
    private String adminNotes;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
