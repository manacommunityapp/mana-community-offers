package com.manacommunity.offers.repository;

import com.manacommunity.offers.domain.enums.SettlementStatus;
import com.manacommunity.offers.entity.SettlementBatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettlementBatchRepository extends JpaRepository<SettlementBatchEntity, String> {

    List<SettlementBatchEntity> findByBusinessIdOrderByCreatedAtDesc(String businessId);

    List<SettlementBatchEntity> findByStatusOrderByCreatedAtDesc(SettlementStatus status);

    Optional<SettlementBatchEntity> findBySettlementNumber(String settlementNumber);
}
