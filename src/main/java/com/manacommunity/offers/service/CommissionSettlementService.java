package com.manacommunity.offers.service;

import com.manacommunity.offers.domain.enums.CommissionStatus;
import com.manacommunity.offers.domain.enums.SettlementStatus;
import com.manacommunity.offers.dto.*;
import com.manacommunity.offers.entity.*;
import com.manacommunity.offers.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommissionSettlementService {

    private final CommissionRecordRepository commissionRepository;
    private final SettlementBatchRepository settlementRepository;
    private final BusinessRepository businessRepository;
    private final CommunityOfferRepository offerRepository;

    @Transactional
    public CommissionRecordEntity recordRedemptionCommission(OfferClaimEntity claim, Double billAmount) {
        BusinessEntity business = businessRepository.findById(claim.getBusinessId()).orElse(null);
        CommunityOfferEntity offer = offerRepository.findById(claim.getOfferId()).orElse(null);

        double effectiveBill = billAmount != null && billAmount > 0
                ? billAmount
                : (offer != null && offer.getCommunityPrice() != null ? offer.getCommunityPrice() : 0.0);

        double discount = 0.0;
        if (offer != null && offer.getRegularPrice() != null && offer.getCommunityPrice() != null) {
            discount = Math.max(0.0, offer.getRegularPrice() - offer.getCommunityPrice());
        }

        // Determine commission rate: offer override -> business rate -> default 5.0%
        double rate = 5.0;
        if (offer != null && offer.getCommissionRateOverridePct() != null) {
            rate = offer.getCommissionRateOverridePct();
        } else if (business != null && business.getCommissionRatePct() != null) {
            rate = business.getCommissionRatePct();
        }

        double commission = Math.round((effectiveBill * (rate / 100.0)) * 100.0) / 100.0;
        double netMerchant = Math.max(0.0, effectiveBill - commission);

        CommissionRecordEntity record = CommissionRecordEntity.builder()
                .businessId(claim.getBusinessId())
                .businessName(business != null ? business.getName() : "Local Business")
                .offerId(claim.getOfferId())
                .offerTitle(offer != null ? offer.getTitle() : "Community Offer")
                .claimId(claim.getId())
                .redemptionCode(claim.getRedemptionCode())
                .residentUserId(claim.getResidentUserId())
                .billAmount(effectiveBill)
                .discountAmount(discount)
                .commissionRatePct(rate)
                .commissionAmount(commission)
                .netMerchantAmount(netMerchant)
                .status(CommissionStatus.PENDING_SETTLEMENT)
                .build();

        CommissionRecordEntity saved = commissionRepository.save(record);

        // Update claim with commission info
        claim.setBillAmount(effectiveBill);
        claim.setDiscountAmount(discount);
        claim.setCommissionAmount(commission);
        claim.setCommissionRecordId(saved.getId());

        log.info("Recorded platform commission ₹{} (rate {}%) on claim {} for business {}",
                commission, rate, claim.getRedemptionCode(), record.getBusinessName());

        return saved;
    }

    @Transactional
    public List<SettlementBatchDto> generateSettlements(GenerateSettlementRequest req) {
        List<CommissionRecordEntity> pendingCommissions = req.getBusinessId() != null && !req.getBusinessId().isBlank()
                ? commissionRepository.findByBusinessIdAndStatus(req.getBusinessId(), CommissionStatus.PENDING_SETTLEMENT)
                : commissionRepository.findByStatus(CommissionStatus.PENDING_SETTLEMENT);

        if (pendingCommissions.isEmpty()) {
            return Collections.emptyList();
        }

        // Group by business ID
        Map<String, List<CommissionRecordEntity>> byBiz = pendingCommissions.stream()
                .collect(Collectors.groupingBy(CommissionRecordEntity::getBusinessId));

        List<SettlementBatchDto> createdBatches = new ArrayList<>();
        LocalDate today = LocalDate.now();
        String datePrefix = today.format(DateTimeFormatter.ofPattern("yyyyMM"));

        for (Map.Entry<String, List<CommissionRecordEntity>> entry : byBiz.entrySet()) {
            String bizId = entry.getKey();
            List<CommissionRecordEntity> records = entry.getValue();
            BusinessEntity biz = businessRepository.findById(bizId).orElse(null);

            double gross = records.stream().mapToDouble(CommissionRecordEntity::getBillAmount).sum();
            double comm = records.stream().mapToDouble(CommissionRecordEntity::getCommissionAmount).sum();
            double net = records.stream().mapToDouble(CommissionRecordEntity::getNetMerchantAmount).sum();

            String settleNum = String.format("SETTLE-%s-%s", datePrefix, UUID.randomUUID().toString().substring(0, 8).toUpperCase());

            SettlementBatchEntity batch = SettlementBatchEntity.builder()
                    .settlementNumber(settleNum)
                    .businessId(bizId)
                    .businessName(biz != null ? biz.getName() : "Local Business")
                    .periodStart(req.getPeriodStart() != null ? req.getPeriodStart() : today.minusDays(30))
                    .periodEnd(req.getPeriodEnd() != null ? req.getPeriodEnd() : today)
                    .totalRedemptions(records.size())
                    .grossSalesAmount(Math.round(gross * 100.0) / 100.0)
                    .totalCommissionAmount(Math.round(comm * 100.0) / 100.0)
                    .netPayoutAmount(Math.round(net * 100.0) / 100.0)
                    .bankAccountNumber(biz != null ? biz.getBankAccountNumber() : null)
                    .bankIfscCode(biz != null ? biz.getBankIfscCode() : null)
                    .bankAccountHolder(biz != null ? biz.getBankAccountHolder() : null)
                    .status(SettlementStatus.PENDING)
                    .notes(req.getNotes() != null ? req.getNotes() : "Monthly community commerce settlement payout")
                    .build();

            SettlementBatchEntity savedBatch = settlementRepository.save(batch);

            // Link commission records to this batch
            for (CommissionRecordEntity cr : records) {
                cr.setSettlementBatchId(savedBatch.getId());
                cr.setStatus(CommissionStatus.SETTLED);
            }
            commissionRepository.saveAll(records);

            log.info("Generated settlement batch {} for business {}: Gross ₹{}, Commission ₹{}, Net Payout ₹{}",
                    settleNum, batch.getBusinessName(), gross, comm, net);

            createdBatches.add(mapToDto(savedBatch));
        }

        return createdBatches;
    }

    @Transactional
    public SettlementBatchDto processSettlement(String settlementId, ProcessSettlementRequest req) {
        SettlementBatchEntity batch = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new IllegalArgumentException("Settlement batch not found: " + settlementId));

        if (batch.getStatus() == SettlementStatus.SETTLED) {
            throw new IllegalStateException("Settlement batch has already been settled");
        }

        batch.setStatus(SettlementStatus.SETTLED);
        batch.setPayoutReference(req.getPayoutReference());
        batch.setSettledAt(LocalDateTime.now());
        batch.setSettledByUserId(req.getAdminUserId() != null ? req.getAdminUserId() : "admin");
        if (req.getNotes() != null) {
            batch.setNotes(batch.getNotes() != null ? batch.getNotes() + " | " + req.getNotes() : req.getNotes());
        }

        SettlementBatchEntity updated = settlementRepository.save(batch);
        log.info("Processed payout for settlement {}: UTR {}", batch.getSettlementNumber(), req.getPayoutReference());
        return mapToDto(updated);
    }

    @Transactional(readOnly = true)
    public List<SettlementBatchDto> getSettlementsForBusiness(String businessId) {
        return settlementRepository.findByBusinessIdOrderByCreatedAtDesc(businessId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SettlementBatchDto> getAllSettlements() {
        return settlementRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SettlementBatchDto getSettlementById(String id) {
        return settlementRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new IllegalArgumentException("Settlement not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<CommissionRecordDto> getCommissionsForBusiness(String businessId) {
        return commissionRepository.findByBusinessIdOrderByCreatedAtDesc(businessId).stream()
                .map(this::mapToCommissionDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommissionRecordDto> getAllCommissions() {
        return commissionRepository.findAll().stream()
                .map(this::mapToCommissionDto)
                .collect(Collectors.toList());
    }

    public SettlementBatchDto mapToDto(SettlementBatchEntity entity) {
        return SettlementBatchDto.builder()
                .id(entity.getId())
                .settlementNumber(entity.getSettlementNumber())
                .businessId(entity.getBusinessId())
                .businessName(entity.getBusinessName())
                .periodStart(entity.getPeriodStart())
                .periodEnd(entity.getPeriodEnd())
                .totalRedemptions(entity.getTotalRedemptions())
                .grossSalesAmount(entity.getGrossSalesAmount())
                .totalCommissionAmount(entity.getTotalCommissionAmount())
                .netPayoutAmount(entity.getNetPayoutAmount())
                .bankAccountNumber(entity.getBankAccountNumber())
                .bankIfscCode(entity.getBankIfscCode())
                .bankAccountHolder(entity.getBankAccountHolder())
                .status(entity.getStatus())
                .payoutReference(entity.getPayoutReference())
                .settledAt(entity.getSettledAt())
                .settledByUserId(entity.getSettledByUserId())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public CommissionRecordDto mapToCommissionDto(CommissionRecordEntity entity) {
        return CommissionRecordDto.builder()
                .id(entity.getId())
                .businessId(entity.getBusinessId())
                .businessName(entity.getBusinessName())
                .offerId(entity.getOfferId())
                .offerTitle(entity.getOfferTitle())
                .claimId(entity.getClaimId())
                .redemptionCode(entity.getRedemptionCode())
                .residentUserId(entity.getResidentUserId())
                .billAmount(entity.getBillAmount())
                .discountAmount(entity.getDiscountAmount())
                .commissionRatePct(entity.getCommissionRatePct())
                .commissionAmount(entity.getCommissionAmount())
                .netMerchantAmount(entity.getNetMerchantAmount())
                .status(entity.getStatus())
                .settlementBatchId(entity.getSettlementBatchId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
