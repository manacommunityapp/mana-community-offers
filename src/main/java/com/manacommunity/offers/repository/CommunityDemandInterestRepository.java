package com.manacommunity.offers.repository;

import com.manacommunity.offers.entity.CommunityDemandInterestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityDemandInterestRepository extends JpaRepository<CommunityDemandInterestEntity, String> {

    List<CommunityDemandInterestEntity> findByDemandId(String demandId);

    Optional<CommunityDemandInterestEntity> findByDemandIdAndResidentUserId(String demandId, String residentUserId);

    boolean existsByDemandIdAndResidentUserId(String demandId, String residentUserId);
}
