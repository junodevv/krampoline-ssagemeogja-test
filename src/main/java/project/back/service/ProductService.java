package project.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import project.back.dto.ApiResponse;
import project.back.dto.ProductDto;
import project.back.entity.Product;
import project.back.etc.commonException.NoContentFoundException;
import project.back.etc.martproduct.MartAndProductMessage;
import project.back.repository.ProductRepository;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 상품 리스트 가져오기
     * 변환 과정에서 상품의 이미지는 Base64 형식으로 인코딩됩니다.
     *
     * @return 이미지 파일 경로를 Base64로 인코딩 하여 ApiResponse 형태로 반환
     * @throws RuntimeException 이미지 파일 처리 중 오류 발생
     * @throws NoContentFoundException 상품 목록이 비어있을 때 발생
     */
    public ApiResponse<List<ProductDto>> findAllProductDtos() {
        List<ProductDto> productDto = productRepository.findAll().stream()
            .map(this::convertToProductDto)
            .collect(Collectors.toList());
        if (productDto.isEmpty()) {
            throw new NoContentFoundException(MartAndProductMessage.EMPTY_PRODUCT_LIST.getMessage());
        }
        return ApiResponse.success(productDto, MartAndProductMessage.LOADED_PRODUCT.getMessage());
    }

    /** 이미지를 Base64로 인코딩하고, ProductDto로 반환 */
    private ProductDto convertToProductDto(Product product) {
        String base64Image =
                "data:image/jpeg;base64," + encodeImageToBase64(product.getProductImgUrl());
        return new ProductDto(product.getProductName(), base64Image);
    }

    /** 이미지 파일을 Base64로 인코딩하여 반환 */
    private String encodeImageToBase64(String imagePath) {
        Resource resource =
                new ClassPathResource("static/images" + (imagePath != null ? imagePath : "/null.png"));
        try {
            if (!resource.exists()) {
                throw new RuntimeException(MartAndProductMessage.NOT_FOUND_PRODUCT_IMG.getMessage());
            }
            byte[] imageData = StreamUtils.copyToByteArray(resource.getInputStream());
            return Base64.getEncoder().encodeToString(imageData);
        } catch (IOException e) {
            throw new RuntimeException(MartAndProductMessage.ERROR_PRODUCT_IMG_PROCESSING.getMessage());
        }
    }
}
