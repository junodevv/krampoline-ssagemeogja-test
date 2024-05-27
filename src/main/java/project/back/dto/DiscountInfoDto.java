package project.back.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@NoArgsConstructor
public class DiscountInfoDto {
    private Long productId;
    private String productName;
    private Long joinId;
    private Long stock;
    private BigDecimal price;
    private BigDecimal discountRate;
    private Long finalPrice;

    public DiscountInfoDto(Long productId, String productName, Long joinId, Long stock, Long price, BigDecimal discountRate) {
        this.productId = productId;
        this.productName = productName;
        this.joinId = joinId;
        this.stock = stock;
        this.price = BigDecimal.valueOf(price);
        this.discountRate = (discountRate != null) ? discountRate : BigDecimal.ZERO;
        this.finalPrice = calculateFinalPrice(BigDecimal.valueOf(price), this.discountRate);
    }

    private Long calculateFinalPrice(BigDecimal price, BigDecimal discountRate) {
        BigDecimal discount = discountRate.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal finalPrice = price.subtract(price.multiply(discount));
        return finalPrice.setScale(0, RoundingMode.HALF_UP).longValue();
    }
}
