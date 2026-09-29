package com.manacommunity.offers.repository;

import com.manacommunity.offers.domain.enums.BusinessVerificationStatus;
import com.manacommunity.offers.entity.CommunityBusinessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityBusinessRepository extends JpaRepository<CommunityBusinessEntity, String> {

    List<CommunityBusinessEntity> findByCommunityId(String communityId);

    List<CommunityBusinessEntity> findByCommunityIdAndStatus(String communityId, BusinessVerificationStatus status);

    List<CommunityBusinessEntity> findByBusinessId(String businessId);

    Optional<CommunityBusinessEntity> findByBusinessIdAndCommunityId(String businessId, String communityId);
}
