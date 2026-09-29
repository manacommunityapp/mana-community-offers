package com.manacommunity.offers.repository;

import com.manacommunity.offers.domain.enums.OfferStatus;
import com.manacommunity.offers.entity.CommunityOfferEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityOfferRepository extends JpaRepository<CommunityOfferEntity, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM CommunityOfferEntity o WHERE o.id = :id")
    Optional<CommunityOfferEntity> findByIdWithPessimisticLock(@Param("id") String id);

    List<CommunityOfferEntity> findByStatusOrderByCreatedAtDesc(OfferStatus status);

    List<CommunityOfferEntity> findByBusinessId(String businessId);

    List<CommunityOfferEntity> findByCategoryIdAndStatus(String categoryId, OfferStatus status);

    @Query("SELECT o FROM CommunityOfferEntity o WHERE o.status = 'PUBLISHED' AND " +
           "(o.validUntil IS NULL OR o.validUntil >= :today) AND " +
           "(o.targetCommunityIds IS NULL OR o.targetCommunityIds LIKE CONCAT('%', :communityId, '%')) " +
           "ORDER BY o.featured DESC, o.createdAt DESC")
    List<CommunityOfferEntity> findActiveOffersForCommunity(@Param("communityId") String communityId,
                                                           @Param("today") LocalDate today);

    @Query("SELECT o FROM CommunityOfferEntity o WHERE o.status = 'PUBLISHED' AND " +
           "(o.validUntil IS NULL OR o.validUntil >= :today) AND " +
           "(o.targetCommunityIds IS NULL OR o.targetCommunityIds LIKE CONCAT('%', :communityId, '%')) AND " +
           "(LOWER(o.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.businessName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<CommunityOfferEntity> searchOffersForCommunity(@Param("communityId") String communityId,
                                                        @Param("today") LocalDate today,
                                                        @Param("query") String query);
}
