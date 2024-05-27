package project.back.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.back.dto.ApiResponse;
import project.back.dto.ProductDto;
import project.back.entity.Product;
import project.back.etc.commonException.NoContentFoundException;
import project.back.etc.martproduct.MartAndProductMessage;
import project.back.repository.ProductRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void 초기_설정() {
        product1 = Product.builder().productId(1L).productName("자유시간").productImgUrl("/freetime.jpeg").build();
        product2 = Product.builder().productId(2L).productName("아이스티").productImgUrl("/null.png").build();
    }

    @Test
    void 상품_목록_출력테스트() {
        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        ApiResponse<List<ProductDto>> response = productService.findAllProductDtos();

        assertThat(response.getData()).hasSize(2);
        assertThat(response.getData().get(0).getProductName()).isEqualTo("자유시간");
        assertThat(response.getData().get(1).getProductName()).isEqualTo("아이스티");
        assertThat(response.getData().get(0).getProductImgUrl()).startsWith("data:image/jpeg;base64,");
        assertThat(response.getData().get(1).getProductImgUrl()).startsWith("data:image/jpeg;base64,");
        assertThat(response.getMessage()).isEqualTo(MartAndProductMessage.LOADED_PRODUCT.getMessage());
   }

    @Test
    @DisplayName("상품 목록이 비어있는 경우")
    void 상품_목록_예외테스트() {
        when(productRepository.findAll()).thenReturn(List.of());

        NoContentFoundException e = assertThrows(NoContentFoundException.class,
                () -> productService.findAllProductDtos());
        assertThat(e.getMessage()).isEqualTo(MartAndProductMessage.EMPTY_PRODUCT_LIST.getMessage());
    }
}
