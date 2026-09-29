package com.manacommunity.offers.repository;

import com.manacommunity.offers.domain.enums.MarketEventStatus;
import com.manacommunity.offers.entity.CommunityMarketEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CommunityMarketEventRepository extends JpaRepository<CommunityMarketEventEntity, String> {

    List<CommunityMarketEventEntity> findByCommunityIdOrderByEventDateAsc(String communityId);

    List<CommunityMarketEventEntity> findByCommunityIdAndStatus(String communityId, MarketEventStatus status);

    List<CommunityMarketEventEntity> findByCommunityIdAndEventDateGreaterThanEqualOrderByEventDateAsc(
            String communityId, LocalDate date);
}
