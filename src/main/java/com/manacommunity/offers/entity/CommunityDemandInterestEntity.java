package com.manacommunity.offers.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_demand_interests", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"demandId", "residentUserId"})
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityDemandInterestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100)
    private String demandId;

    @Column(nullable = false, length = 100)
    private String residentUserId;

    @Column(length = 100)
    private String residentName;

    @Column(length = 50)
    private String flatNumber;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
