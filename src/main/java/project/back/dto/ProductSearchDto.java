package project.back.dto;

import lombok.Builder;
import lombok.Data;
import project.back.entity.Product;

@Data
@Builder
public class ProductSearchDto {
    private Long productId;
    private String productName;
    private String productImgUrl;

    public static ProductSearchDto productToSearchDto(Product product){
        return ProductSearchDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productImgUrl(product.getProductImgUrl())
                .build();
    }
}
