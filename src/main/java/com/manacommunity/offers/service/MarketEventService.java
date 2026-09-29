package com.manacommunity.offers.service;

import com.manacommunity.offers.domain.enums.BoothPackageType;
import com.manacommunity.offers.domain.enums.MarketEventStatus;
import com.manacommunity.offers.domain.enums.VendorApplicationStatus;
import com.manacommunity.offers.dto.*;
import com.manacommunity.offers.entity.*;
import com.manacommunity.offers.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketEventService {

    private final CommunityMarketEventRepository eventRepository;
    private final MarketBoothRepository boothRepository;
    private final MarketVendorApplicationRepository applicationRepository;
    private final MarketVendorPassRepository passRepository;
    private final BusinessRepository businessRepository;

    private final SecureRandom random = new SecureRandom();

    @Transactional(readOnly = true)
    public List<MarketEventResponseDto> getUpcomingEvents(String communityId) {
        LocalDate today = LocalDate.now();
        return eventRepository.findByCommunityIdAndEventDateGreaterThanEqualOrderByEventDateAsc(communityId, today).stream()
                .map(this::mapEventToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MarketEventResponseDto getEventById(String eventId) {
        CommunityMarketEventEntity entity = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Market event not found: " + eventId));
        return mapEventToDto(entity);
    }

    @Transactional
    public MarketEventResponseDto createMarketEvent(CreateMarketEventRequest req) {
        CommunityMarketEventEntity entity = CommunityMarketEventEntity.builder()
                .communityId(req.getCommunityId())
                .communityName(req.getCommunityName() != null ? req.getCommunityName() : "Mana Residency")
                .title(req.getTitle())
                .theme(req.getTheme())
                .description(req.getDescription())
                .bannerImageUrl(req.getBannerImageUrl())
                .eventDate(req.getEventDate())
                .startTime(req.getStartTime() != null ? req.getStartTime() : "16:00")
                .endTime(req.getEndTime() != null ? req.getEndTime() : "21:00")
                .venue(req.getVenue() != null ? req.getVenue() : "Community Clubhouse Lawn")
                .status(MarketEventStatus.UPCOMING)
                .totalBooths(req.getTotalBooths() != null ? req.getTotalBooths() : 12)
                .allocatedBooths(0)
                .expectedVisitors(req.getExpectedVisitors() != null ? req.getExpectedVisitors() : 1500)
                .eventGuidelines(req.getEventGuidelines())
                .entertainmentHighlights(req.getEntertainmentHighlights())
                .build();

        CommunityMarketEventEntity saved = eventRepository.save(entity);

        // Auto-generate standard booth layout (Grid: Row A and Row B)
        initializeDefaultBooths(saved.getId());

        log.info("Created market event: {} ({})", saved.getTitle(), saved.getId());
        return mapEventToDto(saved);
    }

    private void initializeDefaultBooths(String marketEventId) {
        String[] zones = {"Food & Beverages", "Health & Wellness", "Kids & Education", "Fashion & Lifestyle", "Home & Tech", "Fitness & Sports"};
        String[] rowPrefixes = {"A", "B"};

        for (int r = 0; r < 2; r++) {
            String prefix = rowPrefixes[r];
            for (int c = 1; c <= 6; c++) {
                String boothNum = String.format("%s-%02d", prefix, c);
                String zone = zones[(r * 6 + c - 1) % zones.length];
                BoothPackageType pkg = (c <= 2) ? BoothPackageType.PREMIUM_BOOTH : BoothPackageType.BASIC_BOOTH;
                double price = (pkg == BoothPackageType.PREMIUM_BOOTH) ? 3500.0 : 2000.0;

                MarketBoothEntity booth = MarketBoothEntity.builder()
                        .marketEventId(marketEventId)
                        .boothNumber(boothNum)
                        .zone(zone)
                        .packageType(pkg)
                        .price(price)
                        .isOccupied(false)
                        .displayRow(r + 1)
                        .displayCol(c)
                        .build();
                boothRepository.save(booth);
            }
        }
    }

    @Transactional
    public MarketVendorApplicationEntity applyForBooth(ApplyMarketBoothRequest req) {
        BusinessEntity business = businessRepository.findById(req.getBusinessId())
                .orElseThrow(() -> new IllegalArgumentException("Business not found: " + req.getBusinessId()));

        MarketVendorApplicationEntity app = MarketVendorApplicationEntity.builder()
                .marketEventId(req.getMarketEventId())
                .businessId(business.getId())
                .businessName(business.getName())
                .contactPerson(req.getContactPerson() != null ? req.getContactPerson() : business.getName())
                .contactPhone(req.getContactPhone() != null ? req.getContactPhone() : business.getPhone())
                .contactEmail(req.getContactEmail() != null ? req.getContactEmail() : business.getEmail())
                .requestedPackage(req.getRequestedPackage() != null ? req.getRequestedPackage() : BoothPackageType.BASIC_BOOTH)
                .preferredZone(req.getPreferredZone())
                .productsOrServices(req.getProductsOrServices())
                .todaysSpecialOffer(req.getTodaysSpecialOffer())
                .electricityRequired(Boolean.TRUE.equals(req.getElectricityRequired()))
                .tablesRequired(req.getTablesRequired() != null ? req.getTablesRequired() : 1)
                .staffPassesRequired(req.getStaffPassesRequired() != null ? req.getStaffPassesRequired() : 2)
                .status(VendorApplicationStatus.PENDING)
                .feeAmount(req.getRequestedPackage() == BoothPackageType.PREMIUM_BOOTH ? 3500.0 : 2000.0)
                .build();

        return applicationRepository.save(app);
    }

    @Transactional
    public MarketBoothResponseDto assignBooth(String boothId, String businessId, String todaysSpecialOffer) {
        MarketBoothEntity booth = boothRepository.findById(boothId)
                .orElseThrow(() -> new IllegalArgumentException("Booth not found: " + boothId));

        BusinessEntity business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found: " + businessId));

        booth.setIsOccupied(true);
        booth.setAssignedBusinessId(business.getId());
        booth.setAssignedBusinessName(business.getName());
        booth.setAssignedCategory(business.getCategoryName());
        booth.setTodaysSpecialOffer(todaysSpecialOffer);

        MarketBoothEntity saved = boothRepository.save(booth);

        // Update allocated count on market event
        eventRepository.findById(booth.getMarketEventId()).ifPresent(evt -> {
            evt.setAllocatedBooths(evt.getAllocatedBooths() + 1);
            eventRepository.save(evt);
        });

        // Issue Security Vendor Gate Pass
        issueVendorPass(booth.getMarketEventId(), business, booth.getBoothNumber());

        log.info("Assigned booth {} to business {}", booth.getBoothNumber(), business.getName());
        return mapBoothToDto(saved);
    }

    private void issueVendorPass(String marketEventId, BusinessEntity business, String boothNumber) {
        String token = "VPASS-" + (1000 + random.nextInt(9000));
        String qrPayload = String.format("VENDORPASS:%s:%s:%s", marketEventId, business.getId(), token);

        MarketVendorPassEntity pass = MarketVendorPassEntity.builder()
                .marketEventId(marketEventId)
                .businessId(business.getId())
                .businessName(business.getName())
                .boothNumber(boothNumber)
                .staffName(business.getName() + " Staff")
                .staffPhone(business.getPhone())
                .passToken(token)
                .qrCodePayload(qrPayload)
                .validDate(LocalDate.now())
                .checkedIn(false)
                .build();

        passRepository.save(pass);
    }

    @Transactional(readOnly = true)
    public List<MarketBoothResponseDto> getBoothsForEvent(String marketEventId) {
        return boothRepository.findByMarketEventIdOrderByBoothNumberAsc(marketEventId).stream()
                .map(this::mapBoothToDto)
                .collect(Collectors.toList());
    }

    public MarketEventResponseDto mapEventToDto(CommunityMarketEventEntity entity) {
        List<MarketBoothResponseDto> booths = boothRepository.findByMarketEventIdOrderByBoothNumberAsc(entity.getId())
                .stream().map(this::mapBoothToDto).collect(Collectors.toList());

        int total = entity.getTotalBooths() != null ? entity.getTotalBooths() : booths.size();
        int allocated = entity.getAllocatedBooths() != null ? entity.getAllocatedBooths() : (int) booths.stream().filter(MarketBoothResponseDto::getIsOccupied).count();

        return MarketEventResponseDto.builder()
                .id(entity.getId())
                .communityId(entity.getCommunityId())
                .communityName(entity.getCommunityName())
                .title(entity.getTitle())
                .theme(entity.getTheme())
                .description(entity.getDescription())
                .bannerImageUrl(entity.getBannerImageUrl())
                .eventDate(entity.getEventDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .venue(entity.getVenue())
                .status(entity.getStatus())
                .totalBooths(total)
                .allocatedBooths(allocated)
                .availableBooths(Math.max(0, total - allocated))
                .expectedVisitors(entity.getExpectedVisitors())
                .eventGuidelines(entity.getEventGuidelines())
                .entertainmentHighlights(entity.getEntertainmentHighlights())
                .booths(booths)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public MarketBoothResponseDto mapBoothToDto(MarketBoothEntity entity) {
        return MarketBoothResponseDto.builder()
                .id(entity.getId())
                .marketEventId(entity.getMarketEventId())
                .boothNumber(entity.getBoothNumber())
                .zone(entity.getZone())
                .packageType(entity.getPackageType())
                .price(entity.getPrice())
                .isOccupied(entity.getIsOccupied())
                .assignedBusinessId(entity.getAssignedBusinessId())
                .assignedBusinessName(entity.getAssignedBusinessName())
                .assignedCategory(entity.getAssignedCategory())
                .todaysSpecialOffer(entity.getTodaysSpecialOffer())
                .displayRow(entity.getDisplayRow())
                .displayCol(entity.getDisplayCol())
                .build();
    }
}
