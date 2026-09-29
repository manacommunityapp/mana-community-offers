package com.manacommunity.offers.service;

import com.manacommunity.offers.domain.enums.DemandStatus;
import com.manacommunity.offers.dto.CreateDemandRequest;
import com.manacommunity.offers.dto.DemandResponseDto;
import com.manacommunity.offers.entity.BusinessCategoryEntity;
import com.manacommunity.offers.entity.CommunityDemandEntity;
import com.manacommunity.offers.entity.CommunityDemandInterestEntity;
import com.manacommunity.offers.repository.BusinessCategoryRepository;
import com.manacommunity.offers.repository.CommunityDemandInterestRepository;
import com.manacommunity.offers.repository.CommunityDemandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityDemandService {

    private final CommunityDemandRepository demandRepository;
    private final CommunityDemandInterestRepository interestRepository;
    private final BusinessCategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<DemandResponseDto> getDemandsForCommunity(String communityId, String currentUserId) {
        return demandRepository.findByCommunityIdOrderByInterestedFamiliesCountDesc(communityId).stream()
                .map(d -> mapToDto(d, currentUserId))
                .collect(Collectors.toList());
    }

    @Transactional
    public DemandResponseDto createDemand(CreateDemandRequest req) {
        String catName = categoryRepository.findById(req.getCategoryId())
                .map(BusinessCategoryEntity::getName)
                .orElse("General");

        CommunityDemandEntity entity = CommunityDemandEntity.builder()
                .communityId(req.getCommunityId())
                .communityName(req.getCommunityName() != null ? req.getCommunityName() : "Mana Residency")
                .title(req.getTitle())
                .categoryId(req.getCategoryId())
                .categoryName(catName)
                .description(req.getDescription())
                .expectedFrequency(req.getExpectedFrequency())
                .preferredTiming(req.getPreferredTiming())
                .interestedFamiliesCount(1)
                .status(DemandStatus.OPEN)
                .createdByUserId(req.getCreatedByUserId())
                .createdByName(req.getCreatedByName())
                .build();

        CommunityDemandEntity saved = demandRepository.save(entity);

        // Record creator's initial interest
        CommunityDemandInterestEntity interest = CommunityDemandInterestEntity.builder()
                .demandId(saved.getId())
                .residentUserId(req.getCreatedByUserId())
                .residentName(req.getCreatedByName())
                .build();
        interestRepository.save(interest);

        log.info("Created community demand request: {} in community {}", saved.getTitle(), saved.getCommunityId());
        return mapToDto(saved, req.getCreatedByUserId());
    }

    @Transactional
    public DemandResponseDto toggleInterest(String demandId, String residentUserId, String residentName, String flatNumber) {
        CommunityDemandEntity demand = demandRepository.findById(demandId)
                .orElseThrow(() -> new IllegalArgumentException("Demand request not found: " + demandId));

        Optional<CommunityDemandInterestEntity> existing = interestRepository.findByDemandIdAndResidentUserId(demandId, residentUserId);

        if (existing.isPresent()) {
            interestRepository.delete(existing.get());
            demand.setInterestedFamiliesCount(Math.max(1, demand.getInterestedFamiliesCount() - 1));
        } else {
            CommunityDemandInterestEntity interest = CommunityDemandInterestEntity.builder()
                    .demandId(demandId)
                    .residentUserId(residentUserId)
                    .residentName(residentName)
                    .flatNumber(flatNumber)
                    .build();
            interestRepository.save(interest);
            demand.setInterestedFamiliesCount(demand.getInterestedFamiliesCount() + 1);
        }

        CommunityDemandEntity updated = demandRepository.save(demand);
        return mapToDto(updated, residentUserId);
    }

    public DemandResponseDto mapToDto(CommunityDemandEntity entity, String currentUserId) {
        boolean hasExpressed = currentUserId != null &&
                interestRepository.existsByDemandIdAndResidentUserId(entity.getId(), currentUserId);

        return DemandResponseDto.builder()
                .id(entity.getId())
                .communityId(entity.getCommunityId())
                .communityName(entity.getCommunityName())
                .title(entity.getTitle())
                .categoryId(entity.getCategoryId())
                .categoryName(entity.getCategoryName())
                .description(entity.getDescription())
                .expectedFrequency(entity.getExpectedFrequency())
                .preferredTiming(entity.getPreferredTiming())
                .interestedFamiliesCount(entity.getInterestedFamiliesCount())
                .userHasExpressedInterest(hasExpressed)
                .status(entity.getStatus())
                .createdByName(entity.getCreatedByName())
                .fulfilledByBusinessName(entity.getFulfilledByBusinessName())
                .resultingOfferId(entity.getResultingOfferId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
