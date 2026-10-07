package com.manacommunity.offers.repository;

import com.manacommunity.offers.entity.CouponUsageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsageEntity, String> {

    List<CouponUsageEntity> findByResidentUserIdOrderByUsedAtDesc(String residentUserId);

    List<CouponUsageEntity> findByCouponId(String couponId);

    long countByCouponIdAndResidentUserId(String couponId, String residentUserId);

    long countByCouponId(String couponId);
}
