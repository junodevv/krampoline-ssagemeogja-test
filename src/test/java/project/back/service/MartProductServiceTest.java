package project.back.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import project.back.dto.ApiResponse;
import project.back.dto.CartProductDto;
import project.back.dto.DiscountInfoDto;
import project.back.dto.ProductAndDiscountDataDto;
import project.back.entity.*;
import project.back.etc.commonException.NoContentFoundException;
import project.back.etc.martproduct.MartAndProductMessage;
import project.back.repository.*;
import project.back.repository.memberrepository.MemberRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MartProductServiceTest {
    @Mock
    private MartProductRepository martProductRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private JoinMartRepository joinMartRepository;
    @Mock
    private MartRepository martRepository;
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private MartProductService martProductService;

    private Member member;
    private ProductAndDiscountDataDto mockloadData;
    private Map<Long, Long> expectedTotalFinalPrice;
    private final Long memberId = 1L;
    private final Long martAId = 1L;
    private final Long martBId = 2L;
    private final Long joinAId = 1L;
    private final Long joinBId = 3L;
    private final Long productAId = 1L;
    private final Long productBId = 2L;
    @BeforeEach
    void 초기_설정() {
        member = Mockito.mock(Member.class);
        when(member.getMemberId()).thenReturn(memberId);

        JoinMart joinA = JoinMart.builder().joinId(joinAId).build();
        JoinMart joinB = JoinMart.builder().joinId(joinBId).build();
        Mart martA = Mart.builder().id(martAId).joinMart(joinA).build();
        Mart martB = Mart.builder().id(martBId).joinMart(joinB).build();
        Product product1 = Product.builder().productId(productAId).productName("자유시간").productImgUrl("/freetime.jpeg").build();
        Product product2 = Product.builder().productId(productBId).productName("아이스티").productImgUrl("/null.png").build();
        DiscountInfoDto dto1 = new DiscountInfoDto(productAId, "자유시간", joinAId, null, 1000L, BigDecimal.valueOf(10.0));
        DiscountInfoDto dto2 = new DiscountInfoDto(productAId, "자유시간", joinBId, null, 900L, null);
        DiscountInfoDto dto3 = new DiscountInfoDto(productBId, "아이스티", joinAId, null, 2000L, null);
        DiscountInfoDto dto4 = new DiscountInfoDto(productBId, "아이스티", joinBId, null, 2500L, BigDecimal.valueOf(15.0));

        List<CartProductDto> cartProductList = List.of(
                new CartProductDto(3L, member.getMemberId(), productAId),
                new CartProductDto(2L, member.getMemberId(), productBId));
        List<Product> productList = List.of(product1, product2);
        Long numberOfProducts = (long) productList.size();

        lenient().when(joinMartRepository.findJoinMartsSellingAllProducts(productList, numberOfProducts))
                .thenReturn(List.of(joinA, joinB));
        lenient().when(martProductRepository.findDiscountsByProductsAndJoinMarts(productList, List.of(joinA, joinB)))
                .thenReturn(List.of(dto1, dto2, dto3, dto4));

        mockloadData = new ProductAndDiscountDataDto(productList, List.of(dto1, dto2, dto3, dto4), cartProductList);
        expectedTotalFinalPrice = Map.of(
                martA.getId(), 6700L,
                martB.getId(), 6950L
        );
    }

    @Test
    void 마트별_합계금액_출력테스트() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(cartRepository.findCartsByMember(member)).thenReturn(mockloadData.getCartProductList());
        when(productRepository.findById(productAId)).thenReturn(Optional.of(mockloadData.getProductList().get(0)));
        when(productRepository.findById(productBId)).thenReturn(Optional.of(mockloadData.getProductList().get(1)));
        when(martRepository.findByJoinMartJoinId(joinAId)).thenReturn(List.of(Mart.builder().id(martAId).build()));
        when(martRepository.findByJoinMartJoinId(joinBId)).thenReturn(List.of(Mart.builder().id(martBId).build()));

        ApiResponse<Map<Long, Long>> response = martProductService.findMartsByProductIds(memberId);

        assertThat(response.getData()).isEqualTo(expectedTotalFinalPrice);
        assertThat(response.getMessage()).isEqualTo(MartAndProductMessage.LOADED_TOTAL_SUM.getMessage());
    }

    @Test
    void 마트별_세부사항_출력테스트() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(cartRepository.findCartsByMember(member)).thenReturn(mockloadData.getCartProductList());
        when(productRepository.findById(productAId)).thenReturn(Optional.of(mockloadData.getProductList().get(0)));
        when(productRepository.findById(productBId)).thenReturn(Optional.of(mockloadData.getProductList().get(1)));
        when(martRepository.findJoinIdByMartId(martAId)).thenReturn(Optional.of(joinAId));

        ApiResponse<List<DiscountInfoDto>> response = martProductService.findMartInfoByMartId(martAId, memberId);

        List<DiscountInfoDto> filteredDiscountInfo = mockloadData.getDiscountInfoList().stream()
                .filter(dto -> dto.getJoinId().equals(1L))
                .toList();
        assertThat(response.getData()).isEqualTo(filteredDiscountInfo);
        assertThat(response.getMessage()).isEqualTo(MartAndProductMessage.LOADED_MART_DETAILS.getMessage());
    }

    @Test
    @DisplayName("사용자 정보를 찾을 수 없는 경우")
    void 회원_정보_예외테스트() {
      when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

      EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
              () -> martProductService.findMartInfoByMartId(martAId, memberId));
      assertThat(exception.getMessage()).isEqualTo(MartAndProductMessage.NOT_FOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("장바구니가 비어있는 경우")
    void 장바구니_정보_예외테스트() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(cartRepository.findCartsByMember(member)).thenReturn(List.of());

        NoContentFoundException e = assertThrows(NoContentFoundException.class,
                () -> martProductService.findMartsByProductIds(memberId));
        assertThat(e.getMessage()).isEqualTo(MartAndProductMessage.EMPTY_CART.getMessage());
    }

    @Test
    @DisplayName("장바구니 상품목록이 비어있는 경우")
    void 장바구니_상품정보_예외테스트() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(cartRepository.findCartsByMember(member)).thenReturn(mockloadData.getCartProductList());
        when(productRepository.findById(productAId)).thenReturn(Optional.empty());
        when(productRepository.findById(productBId)).thenReturn(Optional.empty());

        NoContentFoundException e = assertThrows(NoContentFoundException.class,
                () -> martProductService.findMartsByProductIds(memberId));
        assertThat(e.getMessage()).isEqualTo(MartAndProductMessage.EMPTY_CART_PRODUCTS.getMessage());
    }

    @Test
    @DisplayName("마트 상세정보를 찾을 수 없는 경우")
    void 마트_상세정보_예외테스트() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(cartRepository.findCartsByMember(member)).thenReturn(mockloadData.getCartProductList());
        when(productRepository.findById(productAId)).thenReturn(Optional.of(mockloadData.getProductList().get(0)));
        when(productRepository.findById(productBId)).thenReturn(Optional.of(mockloadData.getProductList().get(1)));
        when(martRepository.findJoinIdByMartId(martAId)).thenReturn(Optional.empty());

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class,
                () -> martProductService.findMartInfoByMartId(martAId, memberId));
        assertThat(e.getMessage()).isEqualTo(MartAndProductMessage.NOT_FOUND_MART_DETAILS.getMessage());
    }
}