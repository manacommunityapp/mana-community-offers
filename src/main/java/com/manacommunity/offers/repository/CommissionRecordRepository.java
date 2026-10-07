package com.manacommunity.offers.repository;

import com.manacommunity.offers.domain.enums.CommissionStatus;
import com.manacommunity.offers.entity.CommissionRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommissionRecordRepository extends JpaRepository<CommissionRecordEntity, String> {

    List<CommissionRecordEntity> findByBusinessIdOrderByCreatedAtDesc(String businessId);

    List<CommissionRecordEntity> findByBusinessIdAndStatus(String businessId, CommissionStatus status);

    List<CommissionRecordEntity> findByStatus(CommissionStatus status);

    List<CommissionRecordEntity> findBySettlementBatchId(String settlementBatchId);

    List<CommissionRecordEntity> findByOfferId(String offerId);
}
