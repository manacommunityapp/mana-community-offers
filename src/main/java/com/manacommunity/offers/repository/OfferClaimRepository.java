package com.manacommunity.offers.repository;

import com.manacommunity.offers.domain.enums.ClaimStatus;
import com.manacommunity.offers.entity.OfferClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferClaimRepository extends JpaRepository<OfferClaimEntity, String> {

    List<OfferClaimEntity> findByResidentUserIdOrderByClaimedAtDesc(String residentUserId);

    List<OfferClaimEntity> findByResidentUserIdAndStatus(String residentUserId, ClaimStatus status);

    List<OfferClaimEntity> findByOfferId(String offerId);

    List<OfferClaimEntity> findByBusinessIdOrderByClaimedAtDesc(String businessId);

    Optional<OfferClaimEntity> findByRedemptionCode(String redemptionCode);

    boolean existsByOfferIdAndResidentUserIdAndStatus(String offerId, String residentUserId, ClaimStatus status);

    long countByOfferIdAndStatus(String offerId, ClaimStatus status);

    long countByBusinessIdAndStatus(String businessId, ClaimStatus status);
}
