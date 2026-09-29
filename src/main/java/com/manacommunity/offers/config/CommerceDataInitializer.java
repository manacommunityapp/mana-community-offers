package com.manacommunity.offers.config;

import com.manacommunity.offers.domain.enums.*;
import com.manacommunity.offers.entity.*;
import com.manacommunity.offers.repository.*;
import com.manacommunity.offers.service.MarketEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommerceDataInitializer implements CommandLineRunner {

    private final BusinessCategoryRepository categoryRepository;
    private final BusinessRepository businessRepository;
    private final CommunityBusinessRepository communityBusinessRepository;
    private final CommunityOfferRepository offerRepository;
    private final CommunityMarketEventRepository eventRepository;
    private final MarketBoothRepository boothRepository;
    private final CommunityDemandRepository demandRepository;
    private final CommunityDemandInterestRepository interestRepository;
    private final MarketEventService marketEventService;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            log.info("Commerce categories already seeded. Skipping initial data load.");
            return;
        }

        log.info("Seeding Mana Deals & Community Commerce Network starter data...");

        // 1. Categories
        BusinessCategoryEntity catHealth = categoryRepository.save(BusinessCategoryEntity.builder()
                .name("Health & Medical")
                .code("HEALTH_WELLNESS")
                .description("Clinics, dental, diagnostics, pharmacies, and physiotherapy")
                .icon("HeartPulse")
                .displayOrder(1)
                .active(true)
                .build());

        BusinessCategoryEntity catFood = categoryRepository.save(BusinessCategoryEntity.builder()
                .name("Food & Dining")
                .code("FOOD_DINING")
                .description("Restaurants, cafes, bakeries, and organic groceries")
                .icon("Utensils")
                .displayOrder(2)
                .active(true)
                .build());

        BusinessCategoryEntity catBeauty = categoryRepository.save(BusinessCategoryEntity.builder()
                .name("Beauty & Salon")
                .code("BEAUTY_SALON")
                .description("Hair salons, spas, skin clinics, and groomers")
                .icon("Sparkles")
                .displayOrder(3)
                .active(true)
                .build());

        BusinessCategoryEntity catFitness = categoryRepository.save(BusinessCategoryEntity.builder()
                .name("Fitness & Sports")
                .code("FITNESS_SPORTS")
                .description("Gyms, yoga studios, sports academies, and swimming")
                .icon("Dumbbell")
                .displayOrder(4)
                .active(true)
                .build());

        BusinessCategoryEntity catHome = categoryRepository.save(BusinessCategoryEntity.builder()
                .name("Home & Interiors")
                .code("HOME_INTERIORS")
                .description("Home decor, appliances, furniture, and cleaning")
                .icon("Home")
                .displayOrder(5)
                .active(true)
                .build());

        BusinessCategoryEntity catTech = categoryRepository.save(BusinessCategoryEntity.builder()
                .name("Tech & Electronics")
                .code("TECH_ELECTRONICS")
                .description("Laptop/mobile repair, accessories, smart devices")
                .icon("Laptop")
                .displayOrder(6)
                .active(true)
                .build());

        BusinessCategoryEntity catKids = categoryRepository.save(BusinessCategoryEntity.builder()
                .name("Kids & Education")
                .code("KIDS_EDUCATION")
                .description("Tuitions, daycares, toy stores, hobby classes")
                .icon("GraduationCap")
                .displayOrder(7)
                .active(true)
                .build());

        BusinessCategoryEntity catFashion = categoryRepository.save(BusinessCategoryEntity.builder()
                .name("Fashion & Lifestyle")
                .code("FASHION_LIFESTYLE")
                .description("Boutiques, apparel, jewelry, and tailoring")
                .icon("ShoppingBag")
                .displayOrder(8)
                .active(true)
                .build());

        // 2. Businesses
        BusinessEntity bDental = businessRepository.save(BusinessEntity.builder()
                .name("ABC Dental Clinic & Implant Centre")
                .registeredEntityName("ABC Healthcare Pvt Ltd")
                .categoryId(catHealth.getId())
                .categoryName(catHealth.getName())
                .tagline("Gentle dental care for the entire family")
                .description("Multi-specialty dental clinic specializing in teeth cleaning, Invisalign, root canals, and kids dentistry.")
                .address("Plot 42, Gachibowli Main Road, Opp Metro Station")
                .city("Hyderabad")
                .pincode("500032")
                .phone("+91 98765 43210")
                .email("care@abcdental.in")
                .websiteUrl("https://abcdental.in")
                .distanceKm(1.2)
                .verificationStatus(BusinessVerificationStatus.COMMUNITY_PARTNER)
                .partnershipTier(PartnershipTier.GOLD_PARTNER)
                .averageRating(4.9)
                .reviewCount(48)
                .activeDealsCount(2)
                .totalRedemptions(142)
                .active(true)
                .build());

        BusinessEntity bSalon = businessRepository.save(BusinessEntity.builder()
                .name("Glow Unisex Luxury Salon & Spa")
                .registeredEntityName("Glow Spa Ventures")
                .categoryId(catBeauty.getId())
                .categoryName(catBeauty.getName())
                .tagline("Premium hair, skin & bridal styling")
                .description("Award-winning salon with certified stylists. Organic facials, precision haircuts, and rejuvenating head massages.")
                .address("Shop 105, Nexus Mall Road")
                .city("Hyderabad")
                .pincode("500081")
                .phone("+91 91234 56789")
                .email("hello@glowsalon.com")
                .distanceKm(0.8)
                .verificationStatus(BusinessVerificationStatus.COMMUNITY_PARTNER)
                .partnershipTier(PartnershipTier.SILVER_PARTNER)
                .averageRating(4.8)
                .reviewCount(74)
                .activeDealsCount(1)
                .totalRedemptions(210)
                .active(true)
                .build());

        BusinessEntity bGym = businessRepository.save(BusinessEntity.builder()
                .name("FitPulse 24/7 Gym & Crossfit")
                .registeredEntityName("FitPulse Health Club")
                .categoryId(catFitness.getId())
                .categoryName(catFitness.getName())
                .tagline("Train without limits — 24/7 access")
                .description("State of the art gym equipment, certified personal trainers, steam bath, and CrossFit arena.")
                .address("Level 3, Prime Commercial Tower, Hitec City")
                .city("Hyderabad")
                .pincode("500081")
                .phone("+91 99887 76655")
                .email("support@fitpulse.in")
                .distanceKm(1.5)
                .verificationStatus(BusinessVerificationStatus.VERIFIED)
                .partnershipTier(PartnershipTier.SILVER_PARTNER)
                .averageRating(4.7)
                .reviewCount(53)
                .activeDealsCount(1)
                .totalRedemptions(89)
                .active(true)
                .build());

        BusinessEntity bSupermarket = businessRepository.save(BusinessEntity.builder()
                .name("Fresh Basket Organic Supermarket")
                .registeredEntityName("Fresh Organics Retail")
                .categoryId(catFood.getId())
                .categoryName(catFood.getName())
                .tagline("Farm fresh organic fruits, veggies & cold-pressed oils")
                .description("Direct from organic farms. Daily harvest deliveries, gourmet cheeses, cold-pressed oils, and gluten-free staples.")
                .address("Door 12-4, Kondapur Main Road")
                .city("Hyderabad")
                .pincode("500084")
                .phone("+91 94400 11223")
                .email("orders@freshbasket.in")
                .distanceKm(0.5)
                .verificationStatus(BusinessVerificationStatus.COMMUNITY_PARTNER)
                .partnershipTier(PartnershipTier.PLATINUM_PARTNER)
                .averageRating(4.9)
                .reviewCount(112)
                .activeDealsCount(1)
                .totalRedemptions(380)
                .active(true)
                .build());

        BusinessEntity bTech = businessRepository.save(BusinessEntity.builder()
                .name("TechFix Laptop & Smart Care")
                .registeredEntityName("TechFix Solutions LLP")
                .categoryId(catTech.getId())
                .categoryName(catTech.getName())
                .tagline("Same-day doorstep laptop & phone repair")
                .description("Certified chip-level engineers for MacBook, Windows laptops, iPad screen replacement, and RAM/SSD upgrades.")
                .address("Shop 4, Gachibowli Outer Ring Rd")
                .city("Hyderabad")
                .pincode("500032")
                .phone("+91 97000 88990")
                .email("fix@techfix.com")
                .distanceKm(2.0)
                .verificationStatus(BusinessVerificationStatus.VERIFIED)
                .partnershipTier(PartnershipTier.NONE)
                .averageRating(4.6)
                .reviewCount(36)
                .activeDealsCount(1)
                .totalRedemptions(64)
                .active(true)
                .build());

        // 3. Link businesses to Mana Residency
        communityBusinessRepository.save(CommunityBusinessEntity.builder()
                .businessId(bDental.getId())
                .communityId("comm-mana-residency")
                .communityName("Mana Residency")
                .status(BusinessVerificationStatus.COMMUNITY_PARTNER)
                .build());

        // 4. Community Deals / Offers
        offerRepository.save(CommunityOfferEntity.builder()
                .businessId(bDental.getId())
                .businessName(bDental.getName())
                .businessLogoUrl(bDental.getLogoUrl())
                .categoryId(catHealth.getId())
                .categoryName(catHealth.getName())
                .title("Complete Dental Consultation + Digital X-Ray")
                .tagline("Comprehensive oral health assessment by senior dentist")
                .description("Includes oral cavity examination, digital panoramic X-ray, plaque analysis, and personalized treatment roadmap.")
                .dealType(DealType.COMMUNITY_PRICE)
                .regularPrice(1000.0)
                .communityPrice(600.0)
                .discountPercentage(40.0)
                .savingsSummary("Save ₹400 (40% OFF)")
                .termsAndConditions("Valid for one resident per voucher. Prior appointment required. Applicable Monday to Saturday.")
                .eligibilityNote("Exclusive to Mana Residency & Green Valley residents")
                .targetCommunityIds("comm-mana-residency,comm-green-valley")
                .targetCommunityNames("Mana Residency, Green Valley")
                .estimatedAudience(2450)
                .validFrom(LocalDate.now())
                .validUntil(LocalDate.now().plusDays(45))
                .maxClaims(100)
                .claimedCount(28)
                .redeemedCount(19)
                .status(OfferStatus.PUBLISHED)
                .featured(true)
                .viewCount(420)
                .build());

        offerRepository.save(CommunityOfferEntity.builder()
                .businessId(bDental.getId())
                .businessName(bDental.getName())
                .categoryId(catHealth.getId())
                .categoryName(catHealth.getName())
                .title("Ultrasonic Teeth Scaling & Polishing")
                .tagline("Get crystal sparkling clean teeth & fresh breath")
                .description("Advanced painless ultrasonic scaling removes stubborn tartar, stain removal polishing with fluoride protection.")
                .dealType(DealType.COMMUNITY_PRICE)
                .regularPrice(2200.0)
                .communityPrice(1399.0)
                .discountPercentage(36.0)
                .savingsSummary("Save ₹801 (36% OFF)")
                .termsAndConditions("Appointment mandatory. Valid 30 days from claim.")
                .eligibilityNote("Exclusive to Mana Residency residents")
                .targetCommunityIds("comm-mana-residency")
                .targetCommunityNames("Mana Residency")
                .estimatedAudience(1500)
                .validFrom(LocalDate.now())
                .validUntil(LocalDate.now().plusDays(30))
                .maxClaims(60)
                .claimedCount(18)
                .redeemedCount(12)
                .status(OfferStatus.PUBLISHED)
                .featured(false)
                .viewCount(215)
                .build());

        offerRepository.save(CommunityOfferEntity.builder()
                .businessId(bSalon.getId())
                .businessName(bSalon.getName())
                .categoryId(catBeauty.getId())
                .categoryName(catBeauty.getName())
                .title("Designer Haircut + Hair Spa + Scalp Massage")
                .tagline("Signature styling package by senior hair artist")
                .description("Includes wash, deep conditioning L'Oréal hair spa, neck & shoulder pressure point massage, and blow-dry finish.")
                .dealType(DealType.PERCENTAGE_OFF)
                .regularPrice(1500.0)
                .communityPrice(899.0)
                .discountPercentage(40.0)
                .savingsSummary("Save ₹601 (40% OFF)")
                .termsAndConditions("Valid for both men and women. Walk-in or book online.")
                .eligibilityNote("Mana Residency Verified Members")
                .targetCommunityIds("comm-mana-residency")
                .targetCommunityNames("Mana Residency")
                .estimatedAudience(1500)
                .validFrom(LocalDate.now())
                .validUntil(LocalDate.now().plusDays(25))
                .maxClaims(80)
                .claimedCount(42)
                .redeemedCount(31)
                .status(OfferStatus.PUBLISHED)
                .featured(true)
                .viewCount(510)
                .build());

        offerRepository.save(CommunityOfferEntity.builder()
                .businessId(bGym.getId())
                .businessName(bGym.getName())
                .categoryId(catFitness.getId())
                .categoryName(catFitness.getName())
                .title("3-Month Unlimited Fitness & CrossFit Pass")
                .tagline("Get into peak shape with community fitness discount")
                .description("Access all cardio, strength machines, steam room, group Zumba, and certified diet counseling sessions.")
                .dealType(DealType.COMMUNITY_PRICE)
                .regularPrice(7500.0)
                .communityPrice(4999.0)
                .discountPercentage(33.0)
                .savingsSummary("Save ₹2,501 (33% OFF)")
                .termsAndConditions("Valid for new registrations or annual renewals.")
                .eligibilityNote("Mana Community Residents Only")
                .targetCommunityIds("comm-mana-residency")
                .targetCommunityNames("Mana Residency")
                .estimatedAudience(1500)
                .validFrom(LocalDate.now())
                .validUntil(LocalDate.now().plusDays(60))
                .maxClaims(50)
                .claimedCount(21)
                .redeemedCount(14)
                .status(OfferStatus.PUBLISHED)
                .featured(false)
                .viewCount(340)
                .build());

        offerRepository.save(CommunityOfferEntity.builder()
                .businessId(bSupermarket.getId())
                .businessName(bSupermarket.getName())
                .categoryId(catFood.getId())
                .categoryName(catFood.getName())
                .title("Organic Grocery Basket (10kg Assorted Produce)")
                .tagline("Cold-pressed oils, organic rice, and freshly harvested veggies")
                .description("Includes 5kg Sona Masoori organic rice, 1L wood-pressed groundnut oil, 2kg farm potatoes, 2kg onions, and 1kg carrots.")
                .dealType(DealType.BUNDLE)
                .regularPrice(1450.0)
                .communityPrice(1099.0)
                .discountPercentage(24.0)
                .savingsSummary("Save ₹351 (24% OFF)")
                .termsAndConditions("Free doorstep delivery inside Mana Residency premises every morning.")
                .eligibilityNote("Exclusive to Mana Residency")
                .targetCommunityIds("comm-mana-residency")
                .targetCommunityNames("Mana Residency")
                .estimatedAudience(1500)
                .validFrom(LocalDate.now())
                .validUntil(LocalDate.now().plusDays(30))
                .maxClaims(120)
                .claimedCount(55)
                .redeemedCount(44)
                .status(OfferStatus.PUBLISHED)
                .featured(true)
                .viewCount(680)
                .build());

        offerRepository.save(CommunityOfferEntity.builder()
                .businessId(bTech.getId())
                .businessName(bTech.getName())
                .categoryId(catTech.getId())
                .categoryName(catTech.getName())
                .title("Free Doorstep Laptop Deep Cleaning & Thermal Paste Service")
                .tagline("Boost your laptop speed and prevent overheating")
                .description("Internal fan dust removal, Arctic MX-4 thermal paste renewal, keyboard sanitization, and hardware diagnostic check.")
                .dealType(DealType.FREE_SERVICE)
                .regularPrice(800.0)
                .communityPrice(0.0)
                .discountPercentage(100.0)
                .savingsSummary("Free Service (₹800 Value)")
                .termsAndConditions("Free with any diagnostic or software tune-up. Doorstep visit included.")
                .eligibilityNote("Mana Residency Residents")
                .targetCommunityIds("comm-mana-residency")
                .targetCommunityNames("Mana Residency")
                .estimatedAudience(1500)
                .validFrom(LocalDate.now())
                .validUntil(LocalDate.now().plusDays(20))
                .maxClaims(40)
                .claimedCount(30)
                .redeemedCount(22)
                .status(OfferStatus.PUBLISHED)
                .featured(false)
                .viewCount(290)
                .build());

        // 5. Community Market Day / Shopping Fest
        CommunityMarketEventEntity marketDay = eventRepository.save(CommunityMarketEventEntity.builder()
                .communityId("comm-mana-residency")
                .communityName("Mana Residency")
                .title("Mana Family Shopping Fest & Market Day")
                .theme("Local Artisans, Food Carnival & Health Fair")
                .description("Join us for an exciting evening of hyperlocal shopping! 20+ local stalls featuring live food counters, gourmet bakeries, handmade jewelry, organic groceries, and free health checkups.")
                .eventDate(LocalDate.now().plusDays(7))
                .startTime("16:00")
                .endTime("21:00")
                .venue("Community Clubhouse Lawn & Amphitheatre")
                .status(MarketEventStatus.UPCOMING)
                .totalBooths(12)
                .allocatedBooths(4)
                .expectedVisitors(1500)
                .eventGuidelines("1. Eco-friendly packaging mandatory. 2. Low-decibel music only after 8 PM. 3. Security QR pass required for all vendor staff.")
                .entertainmentHighlights("Live Acoustic Music by Society Band (6 PM), Magic Show for Kids (7 PM), Lucky Draw Raffle (8:30 PM)")
                .build());

        // Populate booths
        boothRepository.save(MarketBoothEntity.builder()
                .marketEventId(marketDay.getId())
                .boothNumber("A-01")
                .zone("Health & Wellness")
                .packageType(BoothPackageType.PREMIUM_BOOTH)
                .price(3500.0)
                .isOccupied(true)
                .assignedBusinessId(bDental.getId())
                .assignedBusinessName(bDental.getName())
                .assignedCategory(catHealth.getName())
                .todaysSpecialOffer("Free Dental Checkup + Toothbrush Kit + 40% Off Voucher")
                .displayRow(1)
                .displayCol(1)
                .build());

        boothRepository.save(MarketBoothEntity.builder()
                .marketEventId(marketDay.getId())
                .boothNumber("A-02")
                .zone("Beauty & Wellness")
                .packageType(BoothPackageType.PREMIUM_BOOTH)
                .price(3500.0)
                .isOccupied(true)
                .assignedBusinessId(bSalon.getId())
                .assignedBusinessName(bSalon.getName())
                .assignedCategory(catBeauty.getName())
                .todaysSpecialOffer("Live Hair Styling Demo + 20% Spa Booking Discount")
                .displayRow(1)
                .displayCol(2)
                .build());

        boothRepository.save(MarketBoothEntity.builder()
                .marketEventId(marketDay.getId())
                .boothNumber("A-03")
                .zone("Food & Beverages")
                .packageType(BoothPackageType.BASIC_BOOTH)
                .price(2000.0)
                .isOccupied(true)
                .assignedBusinessId(bSupermarket.getId())
                .assignedBusinessName(bSupermarket.getName())
                .assignedCategory(catFood.getName())
                .todaysSpecialOffer("Organic Mango Pulp Tasting & Flat 20% Off Farm Baskets")
                .displayRow(1)
                .displayCol(3)
                .build());

        boothRepository.save(MarketBoothEntity.builder()
                .marketEventId(marketDay.getId())
                .boothNumber("A-04")
                .zone("Tech & Gadgets")
                .packageType(BoothPackageType.BASIC_BOOTH)
                .price(2000.0)
                .isOccupied(true)
                .assignedBusinessId(bTech.getId())
                .assignedBusinessName(bTech.getName())
                .assignedCategory(catTech.getName())
                .todaysSpecialOffer("Free Screen Protector Fitting on all phones")
                .displayRow(1)
                .displayCol(4)
                .build());

        boothRepository.save(MarketBoothEntity.builder()
                .marketEventId(marketDay.getId())
                .boothNumber("A-05")
                .zone("Fashion & Boutiques")
                .packageType(BoothPackageType.BASIC_BOOTH)
                .price(2000.0)
                .isOccupied(false)
                .displayRow(1)
                .displayCol(5)
                .build());

        boothRepository.save(MarketBoothEntity.builder()
                .marketEventId(marketDay.getId())
                .boothNumber("A-06")
                .zone("Kids & Games")
                .packageType(BoothPackageType.BASIC_BOOTH)
                .price(2000.0)
                .isOccupied(false)
                .displayRow(1)
                .displayCol(6)
                .build());

        // Row B Booths
        for (int c = 1; c <= 6; c++) {
            boothRepository.save(MarketBoothEntity.builder()
                    .marketEventId(marketDay.getId())
                    .boothNumber(String.format("B-%02d", c))
                    .zone(c % 2 == 0 ? "Food Court" : "Home & Decor")
                    .packageType(BoothPackageType.BASIC_BOOTH)
                    .price(2000.0)
                    .isOccupied(false)
                    .displayRow(2)
                    .displayCol(c)
                    .build());
        }

        // 6. Community Demand Board ("Community Wants")
        CommunityDemandEntity dem1 = demandRepository.save(CommunityDemandEntity.builder()
                .communityId("comm-mana-residency")
                .communityName("Mana Residency")
                .title("Weekend swimming coaching for kids (Beginner & Intermediate)")
                .categoryId(catFitness.getId())
                .categoryName(catFitness.getName())
                .description("We need a certified NIS swimming coach to conduct Saturday & Sunday morning batches for age groups 6-12.")
                .expectedFrequency("Every Saturday & Sunday")
                .preferredTiming("7:00 AM - 9:00 AM")
                .interestedFamiliesCount(34)
                .status(DemandStatus.OPEN)
                .createdByUserId("user-ananya")
                .createdByName("Ananya Sharma")
                .build());

        interestRepository.save(CommunityDemandInterestEntity.builder()
                .demandId(dem1.getId())
                .residentUserId("user-ananya")
                .residentName("Ananya Sharma")
                .flatNumber("A-302")
                .build());

        CommunityDemandEntity dem2 = demandRepository.save(CommunityDemandEntity.builder()
                .communityId("comm-mana-residency")
                .communityName("Mana Residency")
                .title("Farm-Fresh Organic Vegetables Sunday Pop-up Stall")
                .categoryId(catFood.getId())
                .categoryName(catFood.getName())
                .description("Direct delivery of certified organic greens, fresh herbs, pesticide-free tomatoes, and seasonal gourds.")
                .expectedFrequency("Every Sunday Morning")
                .preferredTiming("7:30 AM - 11:30 AM")
                .interestedFamiliesCount(86)
                .status(DemandStatus.OPEN)
                .createdByUserId("user-rajesh")
                .createdByName("Rajesh Kumar")
                .build());

        interestRepository.save(CommunityDemandInterestEntity.builder()
                .demandId(dem2.getId())
                .residentUserId("user-rajesh")
                .residentName("Rajesh Kumar")
                .flatNumber("B-501")
                .build());

        CommunityDemandEntity dem3 = demandRepository.save(CommunityDemandEntity.builder()
                .communityId("comm-mana-residency")
                .communityName("Mana Residency")
                .title("Doorstep Bicycle Repair & Annual Maintenance Camp")
                .categoryId(catHome.getId())
                .categoryName(catHome.getName())
                .description("Tuning, brake cable replacement, lubrication, and tire fixes for kids and adult bicycles before winter.")
                .expectedFrequency("Quarterly Camp")
                .preferredTiming("Full Day Sunday")
                .interestedFamiliesCount(28)
                .status(DemandStatus.OPEN)
                .createdByUserId("user-vikram")
                .createdByName("Vikram Mehta")
                .build());

        interestRepository.save(CommunityDemandInterestEntity.builder()
                .demandId(dem3.getId())
                .residentUserId("user-vikram")
                .residentName("Vikram Mehta")
                .flatNumber("C-104")
                .build());

        log.info("Mana Deals & Community Commerce Network starter data seeded successfully.");
    }
}
