package com.manacommunity.offers.repository;

import com.manacommunity.offers.entity.CommunitySponsorshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunitySponsorshipRepository extends JpaRepository<CommunitySponsorshipEntity, String> {

    List<CommunitySponsorshipEntity> findByCommunityIdAndActiveTrue(String communityId);

    List<CommunitySponsorshipEntity> findByBusinessId(String businessId);
}
