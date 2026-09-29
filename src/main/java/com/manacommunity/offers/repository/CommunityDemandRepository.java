package com.manacommunity.offers.repository;

import com.manacommunity.offers.domain.enums.DemandStatus;
import com.manacommunity.offers.entity.CommunityDemandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityDemandRepository extends JpaRepository<CommunityDemandEntity, String> {

    List<CommunityDemandEntity> findByCommunityIdOrderByInterestedFamiliesCountDesc(String communityId);

    List<CommunityDemandEntity> findByCommunityIdAndStatusOrderByInterestedFamiliesCountDesc(
            String communityId, DemandStatus status);
}
