package com.manacommunity.offers.repository;

import com.manacommunity.offers.entity.MarketBoothEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarketBoothRepository extends JpaRepository<MarketBoothEntity, String> {

    List<MarketBoothEntity> findByMarketEventIdOrderByBoothNumberAsc(String marketEventId);

    List<MarketBoothEntity> findByMarketEventIdAndIsOccupiedFalse(String marketEventId);

    Optional<MarketBoothEntity> findByMarketEventIdAndBoothNumber(String marketEventId, String boothNumber);
}
