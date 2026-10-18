package com.project.techstore.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "Nội dung bán hàng đã được cửa hàng xác nhận cho trang chi tiết sản phẩm")
public class ProductStorefrontContentDto {

    @Valid
    private List<StorySection> storySections = new ArrayList<>();
    @Valid
    private Media media = new Media();
    @Valid
    private List<Offer> promotions = new ArrayList<>();
    @Valid
    private List<Offer> paymentOffers = new ArrayList<>();
    @Valid
    private List<BundleOffer> bundleOffers = new ArrayList<>();
    @Valid
    private List<Faq> faqs = new ArrayList<>();
    @Valid
    private List<CityAvailability> cityAvailability = new ArrayList<>();
    @Valid
    private List<InstallmentPlan> installmentPlans = new ArrayList<>();
    @Valid
    private List<TradeInOffer> tradeInOffers = new ArrayList<>();
    @Valid
    private StorePolicy storePolicy = new StorePolicy();
    private List<Long> comparisonProductIds = new ArrayList<>();

    @Data
    @NoArgsConstructor
    public static class StorySection {
        private String heading;
        private String body;
        private String imageUrl;
        private String imageCaption;
    }

    @Data
    @NoArgsConstructor
    public static class Media {
        private String reviewVideoUrl;
        private String unboxingVideoUrl;
        private String unboxingDescription;
        private String view360Url;
    }

    @Data
    @NoArgsConstructor
    public static class Offer {
        private String id;
        @NotBlank
        private String title;
        private String description;
        private String partnerName;
        private String terms;
        private Instant startsAt;
        private Instant endsAt;
        private Integer quota;
        private Integer claimed;
        private BigDecimal discountAmount;
    }

    @Data
    @NoArgsConstructor
    public static class BundleOffer {
        private String id;
        @NotBlank
        private String title;
        private String description;
        @DecimalMin(value = "0.0", inclusive = false)
        private BigDecimal originalPrice;
        @DecimalMin(value = "0.0", inclusive = false)
        private BigDecimal price;
        private Instant startsAt;
        private Instant endsAt;
    }

    @Data
    @NoArgsConstructor
    public static class Faq {
        @NotBlank
        private String question;
        @NotBlank
        private String answer;
    }

    @Data
    @NoArgsConstructor
    public static class CityAvailability {
        @NotBlank
        private String cityCode;
        @NotBlank
        private String cityName;
        @Min(0)
        private Integer storeCount;
        @Min(0)
        private Integer stockCount;
        private String deliveryEstimate;
    }

    @Data
    @NoArgsConstructor
    public static class InstallmentPlan {
        @NotBlank
        private String providerName;
        @Min(1)
        private Integer months;
        private BigDecimal interestRatePercent;
        private BigDecimal monthlyPayment;
        private BigDecimal downPayment;
        private String terms;
        private Instant startsAt;
        private Instant endsAt;
    }

    @Data
    @NoArgsConstructor
    public static class TradeInOffer {
        @NotBlank
        private String deviceModel;
        private BigDecimal maximumSubsidy;
        private BigDecimal minimumTopUp;
        private BigDecimal maximumTopUp;
        private String condition;
        private String terms;
        private Instant startsAt;
        private Instant endsAt;
    }

    @Data
    @NoArgsConstructor
    public static class StorePolicy {
        private String officialProductText;
        private String warrantyText;
        private String exchangeText;
        private String shippingText;
        private String hotline;
        private String hotlineHours;
    }
}
