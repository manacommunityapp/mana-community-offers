package com.manacommunity.offers.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "business_reviews")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100)
    private String businessId;

    @Column(length = 100)
    private String offerId;

    @Column(nullable = false, length = 100)
    private String residentUserId;

    @Column(length = 150)
    private String residentName;

    @Column(length = 50)
    private String unitNumber;

    @Column(nullable = false)
    private Integer rating; // 1 to 5

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Builder.Default
    private Boolean verifiedBuyer = true;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
