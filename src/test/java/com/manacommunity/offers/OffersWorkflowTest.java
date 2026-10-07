package com.manacommunity.offers;

import com.manacommunity.offers.domain.enums.*;
import com.manacommunity.offers.dto.*;
import com.manacommunity.offers.entity.*;
import com.manacommunity.offers.repository.*;
import com.manacommunity.offers.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class OffersWorkflowTest {

    @Autowired
    private BusinessService businessService;

    @Autowired
    private CommunityOfferService offerService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CommissionSettlementService settlementService;

    @Autowired
    private CommerceAnalyticsService analyticsService;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private BusinessCategoryRepository categoryRepository;

    @Autowired
    private CommunityOfferRepository offerRepository;

    @Autowired
    private OfferClaimRepository claimRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CommissionRecordRepository commissionRepository;

    @Autowired
    private SettlementBatchRepository settlementRepository;

    private String testCategoryId;

    @BeforeEach
    void setup() {
        // Query existing category seeded by CommerceDataInitializer or create if missing
        BusinessCategoryEntity cat = categoryRepository.findByCode("FOOD_DINING")
                .orElseGet(() -> categoryRepository.save(BusinessCategoryEntity.builder()
                        .name("Food & Dining")
                        .code("FOOD_DINING")
                        .displayOrder(1)
                        .active(true)
                        .build()));
        testCategoryId = cat.getId();
    }

    @Test
    @DisplayName("Complete Offers Lifecycle: Merchant KYC -> Coupons -> Limits -> QR Redemption -> Commissions -> Settlement")
    void testEndToEndOffersLifecycle() {
        // 1. Merchant Registration & KYC Submission
        RegisterBusinessRequest regReq = RegisterBusinessRequest.builder()
                .name("Artisan Cafe & Bakehouse")
                .registeredEntityName("Artisan Foods Pvt Ltd")
                .categoryId(testCategoryId)
                .address("Shop 10, Mana Residency Arcade")
                .city("Hyderabad")
                .phone("+91 99887 76655")
                .ownerUserId("user-owner-1")
                .build();

        BusinessResponseDto bizDto = businessService.registerBusiness(regReq);
        assertNotNull(bizDto.getId());
        assertEquals(BusinessVerificationStatus.PENDING, bizDto.getVerificationStatus());

        // Submit KYC & Banking details
        MerchantKycRequest kycReq = MerchantKycRequest.builder()
                .registeredEntityName("Artisan Foods Pvt Ltd")
                .gstin("36AABCU9603R1ZM")
                .businessRegistrationNumber("CIN-U15400TG2020PTC145000")
                .kycDocumentUrl("https://storage.mana.internal/kyc/artisan-gst.pdf")
                .bankAccountNumber("91998877665501")
                .bankIfscCode("HDFC0001234")
                .bankAccountHolder("Artisan Foods Pvt Ltd")
                .bankName("HDFC Bank")
                .build();

        BusinessResponseDto kycBiz = businessService.submitKyc(bizDto.getId(), kycReq);
        assertEquals("36AABCU9603R1ZM", kycBiz.getGstin());
        assertEquals("HDFC0001234", kycBiz.getBankIfscCode());

        // Admin verifies merchant with 4.5% negotiated commission rate
        VerifyMerchantRequest verifyReq = VerifyMerchantRequest.builder()
                .status(BusinessVerificationStatus.VERIFIED)
                .partnershipTier(PartnershipTier.GOLD_PARTNER)
                .commissionRatePct(4.5)
                .adminUserId("admin-super-1")
                .build();

        BusinessResponseDto verifiedBiz = businessService.verifyMerchant(bizDto.getId(), verifyReq);
        assertEquals(BusinessVerificationStatus.VERIFIED, verifiedBiz.getVerificationStatus());
        assertEquals(PartnershipTier.GOLD_PARTNER, verifiedBiz.getPartnershipTier());
        assertEquals(4.5, verifiedBiz.getCommissionRatePct());

        // 2. Coupon Creation, Min Order & Usage Limit Enforcement
        CreateCouponRequest couponReq = CreateCouponRequest.builder()
                .code("ARTISAN25")
                .title("25% Off Artisan Brunch")
                .description("25% discount on bills above ₹400, max discount ₹150")
                .businessId(bizDto.getId())
                .communityId("comm-mana-residency")
                .discountType(CouponDiscountType.PERCENTAGE)
                .discountValue(25.0)
                .minOrderAmount(400.0)
                .maxDiscountAmount(150.0)
                .usageLimitTotal(100)
                .usageLimitPerUser(1)
                .validFrom(LocalDate.now())
                .validUntil(LocalDate.now().plusDays(30))
                .createdByUserId("user-owner-1")
                .build();

        CouponDto coupon = couponService.createCoupon(couponReq);
        assertEquals("ARTISAN25", coupon.getCode());
        assertEquals(CouponStatus.ACTIVE, coupon.getStatus());

        // Test below minimum order requirement
        ValidateCouponRequest invalidMinOrderReq = ValidateCouponRequest.builder()
                .code("ARTISAN25")
                .orderAmount(300.0)
                .residentUserId("user-resident-1")
                .build();
        CouponValidationResponse belowMinResp = couponService.validateCoupon(invalidMinOrderReq);
        assertFalse(belowMinResp.isValid());
        assertTrue(belowMinResp.getMessage().contains("Minimum order"));

        // Test valid coupon application
        ValidateCouponRequest validOrderReq = ValidateCouponRequest.builder()
                .code("ARTISAN25")
                .orderAmount(800.0)
                .residentUserId("user-resident-1")
                .build();
        CouponValidationResponse validResp = couponService.validateCoupon(validOrderReq);
        assertTrue(validResp.isValid());
        // 25% of 800 = 200, but capped at max discount 150
        assertEquals(150.0, validResp.getCalculatedDiscount());
        assertEquals(650.0, validResp.getFinalPayableAmount());

        // Apply coupon
        CouponValidationResponse appliedResp = couponService.applyCoupon(validOrderReq);
        assertTrue(appliedResp.isValid());

        // Attempting to apply again with same resident should fail due to per-user limit
        assertThrows(IllegalStateException.class, () -> couponService.applyCoupon(validOrderReq));

        // 3. Community Offer Creation & Per-User Claim Limit
        CreateOfferRequest offerReq = CreateOfferRequest.builder()
                .businessId(bizDto.getId())
                .categoryId(testCategoryId)
                .title("Special Gourmet Brunch Combo")
                .dealType(DealType.COMMUNITY_PRICE)
                .regularPrice(1000.0)
                .communityPrice(600.0)
                .maxClaims(10)
                .maxClaimsPerUser(1)
                .validFrom(LocalDate.now())
                .validUntil(LocalDate.now().plusDays(15))
                .build();

        OfferResponseDto offerDto = offerService.createOffer(offerReq);
        assertNotNull(offerDto.getId());
        assertEquals(40.0, offerDto.getDiscountPercentage());
        assertEquals(1, offerDto.getMaxClaimsPerUser());

        // Resident claims offer
        ClaimOfferRequest claimReq = ClaimOfferRequest.builder()
                .offerId(offerDto.getId())
                .communityId("comm-mana-residency")
                .residentUserId("user-resident-1")
                .residentName("Sandeep Patil")
                .unitNumber("B-402")
                .build();

        ClaimResponseDto claim = offerService.claimOffer(claimReq);
        assertNotNull(claim.getRedemptionCode());
        assertNotNull(claim.getCounterPin());
        assertNotNull(claim.getQrPayload());
        assertEquals(ClaimStatus.ACTIVE, claim.getStatus());
        assertTrue(claim.getQrPayload().startsWith("MANADEAL:"));

        // Second claim by same resident must be rejected due to maxClaimsPerUser = 1
        assertThrows(IllegalStateException.class, () -> offerService.claimOffer(claimReq));

        // 4. QR Verification & Idempotent Redemption
        QrVerificationResponse qrVerification = offerService.verifyQr(claim.getQrPayload());
        assertTrue(qrVerification.isValid());
        assertEquals(claim.getRedemptionCode(), qrVerification.getRedemptionCode());
        assertEquals("Sandeep Patil", qrVerification.getResidentName());
        assertFalse(qrVerification.isAlreadyRedeemed());

        // Store staff redeems voucher with bill amount
        RedeemOfferRequest redeemReq = RedeemOfferRequest.builder()
                .redemptionCode(claim.getRedemptionCode())
                .counterPin(claim.getCounterPin())
                .businessId(bizDto.getId())
                .billAmount(600.0)
                .staffName("Cashier Raj")
                .notes("Dine-in lunch table 4")
                .build();

        ClaimResponseDto redeemed = offerService.redeemOffer(redeemReq);
        assertEquals(ClaimStatus.REDEEMED, redeemed.getStatus());
        assertNotNull(redeemed.getRedeemedAt());
        assertEquals(600.0, redeemed.getBillAmount());

        // QR verification now reports already redeemed
        QrVerificationResponse qrVerifyAfter = offerService.verifyQr(claim.getQrPayload());
        assertFalse(qrVerifyAfter.isValid());
        assertTrue(qrVerifyAfter.isAlreadyRedeemed());

        // Cannot redeem again
        assertThrows(IllegalStateException.class, () -> offerService.redeemOffer(redeemReq));

        // 5. Commission Ledger Tracking
        List<CommissionRecordDto> bizCommissions = settlementService.getCommissionsForBusiness(bizDto.getId());
        assertEquals(1, bizCommissions.size());
        CommissionRecordDto comm = bizCommissions.get(0);
        assertEquals(600.0, comm.getBillAmount());
        assertEquals(4.5, comm.getCommissionRatePct());
        // 4.5% of 600 = 27.0
        assertEquals(27.0, comm.getCommissionAmount());
        assertEquals(573.0, comm.getNetMerchantAmount());
        assertEquals(CommissionStatus.PENDING_SETTLEMENT, comm.getStatus());

        // 6. Settlement Batch Generation & Bank Payout
        GenerateSettlementRequest settleGenReq = GenerateSettlementRequest.builder()
                .businessId(bizDto.getId())
                .adminUserId("admin-finance-1")
                .notes("October Bi-weekly Cycle")
                .build();

        List<SettlementBatchDto> batches = settlementService.generateSettlements(settleGenReq);
        assertEquals(1, batches.size());
        SettlementBatchDto batch = batches.get(0);
        assertTrue(batch.getSettlementNumber().startsWith("SETTLE-"));
        assertEquals(1, batch.getTotalRedemptions());
        assertEquals(600.0, batch.getGrossSalesAmount());
        assertEquals(27.0, batch.getTotalCommissionAmount());
        assertEquals(573.0, batch.getNetPayoutAmount());
        assertEquals(SettlementStatus.PENDING, batch.getStatus());

        // Process payout with Bank UTR reference
        ProcessSettlementRequest processReq = ProcessSettlementRequest.builder()
                .payoutReference("UTR-HDFC-991283741029")
                .adminUserId("admin-finance-1")
                .notes("NEFT Transfer Confirmed")
                .build();

        SettlementBatchDto settledBatch = settlementService.processSettlement(batch.getId(), processReq);
        assertEquals(SettlementStatus.SETTLED, settledBatch.getStatus());
        assertEquals("UTR-HDFC-991283741029", settledBatch.getPayoutReference());
        assertNotNull(settledBatch.getSettledAt());

        // 7. Campaign Analytics
        CampaignAnalyticsDto campAnalytics = offerService.getCampaignAnalytics(offerDto.getId());
        assertEquals(1, campAnalytics.getClaimedCount());
        assertEquals(1, campAnalytics.getRedeemedCount());
        assertEquals(100.0, campAnalytics.getRedemptionRatePct());
        assertEquals(400.0, campAnalytics.getTotalGmvDiscounted());
        assertEquals(600.0, campAnalytics.getTotalSalesGmv());

        // Platform commerce analytics
        CommerceAnalyticsDto platformStats = analyticsService.getCommunityCommerceAnalytics("comm-mana-residency");
        assertTrue(platformStats.getTotalClaims() >= 1);
        assertTrue(platformStats.getTotalRedemptions() >= 1);
        assertTrue(platformStats.getTotalCommissionsEarned() >= 27.0);

        // 8. Maintenance Sweep for Expiry
        int expiredRecords = offerService.expireOutdatedRecords();
        assertTrue(expiredRecords >= 0);
    }
}
