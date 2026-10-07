package com.manacommunity.offers.repository;

import com.manacommunity.offers.domain.enums.CouponStatus;
import com.manacommunity.offers.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, String> {

    Optional<CouponEntity> findByCodeIgnoreCaseAndActiveTrue(String code);

    List<CouponEntity> findByBusinessIdAndActiveTrue(String businessId);

    List<CouponEntity> findByActiveTrueOrderByCreatedAtDesc();

    List<CouponEntity> findByStatusAndValidUntilBefore(CouponStatus status, LocalDate date);
}
