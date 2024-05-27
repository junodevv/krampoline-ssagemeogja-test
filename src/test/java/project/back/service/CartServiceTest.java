package project.back.service;

import static org.assertj.core.api.Assertions.*;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.back.dto.ApiResponse;
import project.back.dto.CartDto;
import project.back.dto.ProductSearchDto;
import project.back.entity.Product;
import project.back.etc.commonException.ConflictException;
import project.back.etc.commonException.NoContentFoundException;
import project.back.repository.ProductRepository;

@SpringBootTest
class CartServiceTest {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartService cartService;

    private Long commonMemberId;
    private Long commonProductId;
    private CartDto commonCartDto;

    @BeforeEach
    void setUp() {
        commonMemberId = 1L;
        commonProductId = 1L;
        commonCartDto = CartDto.builder()
                .productId(commonProductId)
                .build();
    }

    @Test
    @DisplayName("장바구니 조회 테스트")
    void 장바구니_조회_테스트() {
        ApiResponse<List<CartDto>> result = cartService.getCartsByMemberId(commonMemberId);

        assertThat(result.getData()).isNotNull();
    }

    @Test
    @DisplayName("장바구니 조회 예외 테스트")
    void 장바구니_조회_예외_테스트_EntityNotFoundException() {
        Long memberId = 1000L;

        assertThatThrownBy(() -> cartService.getCartsByMemberId(memberId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("상품검색테스트")
    @Transactional
    void 재료검색테스트() {
        String testProductName = "이준오의 당근";
        Product product1 = new Product(testProductName, "temp");
        productRepository.save(product1);

        ApiResponse<List<ProductSearchDto>> result1 = cartService.findAllByProductName(testProductName);
        List<ProductSearchDto> resultDatas = result1.getData();
        // "당근"을 포함하는 모든 상품을 가져오는지
        ApiResponse<List<ProductSearchDto>> result2 = cartService.findAllByProductName("당근");
        List<ProductSearchDto> result2Datas = result2.getData();

        assertThat(resultDatas.size()).isEqualTo(1);
        assertThat(result2Datas.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("상품검색 예외테스트")
    void 상품검색_예외테스트() {
        String productName = "상품이름검색 ㅋㅋ 이런 상품은 없겠지";

        assertThatThrownBy(() -> cartService.findAllByProductName(productName))
                .isInstanceOf(NoContentFoundException.class);
    }

    @Test
    @DisplayName("상품추가 테스트")
    @Transactional
    void 상품추가_테스트() {
        ApiResponse<List<CartDto>> result = cartService.addProduct(commonCartDto, commonMemberId);
        CartDto addedProduct = result.getData().stream()
                .filter(c -> c.getProductId() == commonProductId)
                .toList()
                .get(0);

        assertThat(addedProduct).isNotNull();
    }

    @ParameterizedTest
    @CsvSource(value = {"1000,1", "1,10000"})
    @DisplayName("상품추가 예외테스트: 사용자 없음(1000), 상품 없음(10000) ")
    void 상품추가_예외테스트_EntityNotFoundException(Long memberId, Long productId) {
        CartDto cartDto = CartDto.builder()
                .productId(productId)
                .build();

        assertThatThrownBy(() -> cartService.addProduct(cartDto, memberId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("상품추가 예외테스트: 중복된 상품")
    @Transactional
    void 상품추가_예외테스트_중복된_상품_ConflictException() {
        cartService.addProduct(commonCartDto, commonMemberId); // 첫번째 저장

        assertThatThrownBy(() -> cartService.addProduct(commonCartDto, commonMemberId)) // 두번째 저장 -> ConflictException
                .isInstanceOf(ConflictException.class);

    }

    @ParameterizedTest
    @DisplayName("상품 수량 변경 테스트(직접입력)")
    @CsvSource(value = {"6, 6", "5, 5", "1, 1"})
    @Transactional
    void 상품_수량_변경_테스트(Long count, Long expected) {
        cartService.addProduct(commonCartDto, commonMemberId); // 장바구니에 1L인 상품추가

        ApiResponse<List<CartDto>> response = cartService.updateQuantity(commonProductId, count, commonMemberId);
        Long result = response.getData().stream()
                .filter(c -> Objects.equals(c.getProductId(), commonProductId))
                .toList()
                .get(0).getQuantity();

        assertThat(result).isEqualTo(expected);
    }
    @Test
    @DisplayName("상품 수량 변경 테스트(증가)")
    @Transactional
    void 상품_수량_변경_테스트_증가(){
        cartService.addProduct(commonCartDto, commonMemberId); // 장바구니에 1L인 상품추가

        ApiResponse<List<CartDto>> response = cartService.plusQuantity(commonProductId, commonMemberId);
        Long result = response.getData().stream()
                .filter(c -> Objects.equals(c.getProductId(), commonProductId))
                .toList()
                .get(0).getQuantity();

        assertThat(result).isEqualTo(2L);
    }
    @Test
    @DisplayName("상품 수량 변경 테스트(감소)")
    @Transactional
    void 상품_수량_변경_테스트_감소(){
        cartService.addProduct(commonCartDto, commonMemberId); // 장바구니에 1L인 상품추가
        cartService.updateQuantity(commonProductId, 6L, commonMemberId);

        ApiResponse<List<CartDto>> response = cartService.minusQuantity(commonProductId, commonMemberId);
        Long result = response.getData().stream()
                .filter(c -> Objects.equals(c.getProductId(), commonProductId))
                .toList()
                .get(0).getQuantity();

        assertThat(result).isEqualTo(5L);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -2})
    @DisplayName("상품 수량 변경(직접입력) 예외 테스트: 직접입력한 값이 0이하인 경우")
    @Transactional
    void 상품_수량_변경_예외_테스트_직접입력_IllegalArgumentException(Long count) {
        cartService.addProduct(commonCartDto, commonMemberId); // 장바구니에 1L인 상품추가, quantity=1

        assertThatThrownBy(() -> cartService.updateQuantity(commonProductId, count, commonMemberId))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    @DisplayName("상품 수량 변경(감소) 예외 테스트: -한 값이 0 인 경우")
    @Transactional
    void 상품_수량_변경_예외_테스트_감소_IllegalArgumentException(){
        cartService.addProduct(commonCartDto, commonMemberId); // 장바구니에 1L인 상품추가, quantity=1
        assertThatThrownBy(() -> cartService.minusQuantity(commonProductId, commonMemberId))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @ParameterizedTest
    @CsvSource(value = {"3,1", "1,1000", "1,1"})
    @DisplayName("상품 수량 변경 예외 테스트: 사용자 없음, 상품 없음, 장바구니에 해당 상품없음")
    @Transactional
    void 상품_수량_변경_예외_테스트_EntityNotFoundException(Long memberId, Long productId) {
        Long count = 1L;
        assertThatThrownBy(() -> cartService.updateQuantity(productId, count, memberId))
                .isInstanceOf(EntityNotFoundException.class);
        assertThatThrownBy(() -> cartService.plusQuantity(productId, memberId))
                .isInstanceOf(EntityNotFoundException.class);
        assertThatThrownBy(() -> cartService.minusQuantity(productId, memberId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("(개별)상품 삭제 테스트")
    @Transactional
    void 개별_상품_삭제_테스트() {
        cartService.addProduct(commonCartDto, commonMemberId);
        int beforeSize = cartService.getCartsByMemberId(commonMemberId).getData().size();

        int afterSize = cartService.deleteProduct(commonProductId, commonMemberId).getData().size();

        assertThat(afterSize).isEqualTo(beforeSize - 1);
    }

    @Test
    @DisplayName("(개별)상품 삭제 예외 테스트: 해당 상품이 장바구니에 없는경우")
    @Transactional
    void 개별_상품_삭제_예외_테스트_EntityNotFoundException() {
        assertThatThrownBy(() -> cartService.deleteProduct(commonProductId, commonMemberId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("(전체)상품 삭제 테스트")
    @Transactional
    void 전체_상품_삭제_테스트() {
        int resultSize = cartService.deleteAllProduct(commonMemberId).getData().size();

        assertThat(resultSize).isEqualTo(0);
    }
}