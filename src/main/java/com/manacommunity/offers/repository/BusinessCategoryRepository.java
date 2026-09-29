package com.manacommunity.offers.repository;

import com.manacommunity.offers.entity.BusinessCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessCategoryRepository extends JpaRepository<BusinessCategoryEntity, String> {
    List<BusinessCategoryEntity> findByActiveTrueOrderByDisplayOrderAsc();
    Optional<BusinessCategoryEntity> findByCode(String code);
}
