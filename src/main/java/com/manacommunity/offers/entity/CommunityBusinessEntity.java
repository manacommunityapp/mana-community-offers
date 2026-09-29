package com.manacommunity.offers.entity;

import com.manacommunity.offers.domain.enums.BusinessVerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_businesses", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"businessId", "communityId"})
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityBusinessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100)
    private String businessId;

    @Column(nullable = false, length = 100)
    private String communityId;

    @Column(length = 150)
    private String communityName;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BusinessVerificationStatus status = BusinessVerificationStatus.PENDING;

    @Column(length = 100)
    private String approvedByUserId;

    private LocalDateTime approvedAt;

    @Column(length = 255)
    private String rejectionReason;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
