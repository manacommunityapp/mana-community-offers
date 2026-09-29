package com.manacommunity.offers.repository;

import com.manacommunity.offers.domain.enums.VendorApplicationStatus;
import com.manacommunity.offers.entity.MarketVendorApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketVendorApplicationRepository extends JpaRepository<MarketVendorApplicationEntity, String> {

    List<MarketVendorApplicationEntity> findByMarketEventId(String marketEventId);

    List<MarketVendorApplicationEntity> findByBusinessId(String businessId);

    List<MarketVendorApplicationEntity> findByMarketEventIdAndStatus(String marketEventId, VendorApplicationStatus status);
}
