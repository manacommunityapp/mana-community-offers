package com.manacommunity.offers.repository;

import com.manacommunity.offers.entity.BusinessReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessReviewRepository extends JpaRepository<BusinessReviewEntity, String> {

    List<BusinessReviewEntity> findByBusinessIdOrderByCreatedAtDesc(String businessId);

    List<BusinessReviewEntity> findByResidentUserId(String residentUserId);
}
