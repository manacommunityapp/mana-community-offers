package com.manacommunity.offers.repository;

import com.manacommunity.offers.domain.enums.BusinessVerificationStatus;
import com.manacommunity.offers.entity.BusinessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<BusinessEntity, String> {

    List<BusinessEntity> findByActiveTrueOrderByAverageRatingDesc();

    List<BusinessEntity> findByCategoryIdAndActiveTrue(String categoryId);

    List<BusinessEntity> findByVerificationStatus(BusinessVerificationStatus status);

    List<BusinessEntity> findByOwnerUserId(String ownerUserId);

    @Query("SELECT b FROM BusinessEntity b WHERE b.active = true AND " +
           "(LOWER(b.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.tagline) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.categoryName) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<BusinessEntity> searchBusinesses(@Param("query") String query);
}
