package com.manacommunity.offers.repository;

import com.manacommunity.offers.entity.MarketVendorPassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarketVendorPassRepository extends JpaRepository<MarketVendorPassEntity, String> {

    List<MarketVendorPassEntity> findByMarketEventId(String marketEventId);

    List<MarketVendorPassEntity> findByBusinessId(String businessId);

    Optional<MarketVendorPassEntity> findByPassToken(String passToken);
}
