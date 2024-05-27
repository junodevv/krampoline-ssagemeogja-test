package project.back.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.back.dto.ApiResponse;
import project.back.dto.CartDto;
import project.back.dto.ProductSearchDto;
import project.back.entity.Cart;
import project.back.entity.Member;
import project.back.entity.Product;
import project.back.etc.commonException.ConflictException;
import project.back.etc.commonException.NoContentFoundException;
import project.back.etc.cart.enums.CartErrorMessage;
import project.back.etc.cart.enums.CartSuccessMessage;
import project.back.repository.CartRepository;
import project.back.repository.ProductRepository;
import project.back.repository.memberrepository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private static final Long FIRST_ADD_VALUE = 1L;

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    /**
     * 장바구니 목록 조회
     *
     * @param memberId 사용자 고유번호
     * @return 장바구니 목록
     * @throws EntityNotFoundException 사용자정보를 찾을 수 없는경우
     */
    @Transactional
    public ApiResponse<List<CartDto>> getCartsByMemberId(Long memberId) {
        Member member = getMemberByMemberId(memberId);
        List<CartDto> cartDtos = cartRepository.findByMemberEquals(member).stream()
                .map(CartDto::CartToDto)
                .toList();
        return ApiResponse.success(cartDtos, CartSuccessMessage.GET.getMessage());
    }

    /**
     * 상품 검색
     *
     * @param productName 상품이름(String)
     * @return 상품이름을 포함하는 상품들(과 이미지)
     * @throws NoContentFoundException productName 을 포함하는 상품이 없는경우
     */
    public ApiResponse<List<ProductSearchDto>> findAllByProductName(String productName) {
        List<Product> products = productRepository.findAllByProductNameContaining(productName);

        validateProducts(productName, products);

        List<ProductSearchDto> ProductSearchDtos = products.stream()
                .map(ProductSearchDto::productToSearchDto)
                .toList();

        return ApiResponse.success(ProductSearchDtos, CartSuccessMessage.SEARCH.getMessage());
    }
    /**
     * 상품을 장바구니에 저장(등록)
     *
     * @param cartDto  카트에 담길 상품 정보(productId를 가진 객체)
     * @param memberId 유저정보를 가져오기위한 memberId
     * @return 장바구니 목록(CartDto)
     * @throws EntityNotFoundException 사용자 정보나 상품 정보를 찾을 수 없는 경우
     * @throws ConflictException       같은 상품이 이미 담겨있는 경우
     */
    @Transactional
    public ApiResponse<List<CartDto>> addProduct(CartDto cartDto, Long memberId) {
        Member member = getMemberByMemberId(memberId);
        Product product = getProductByProductId(cartDto.getProductId());

        checkAlreadyExist(member, product);

        Cart cart = Cart.builder()
                .product(product)
                .member(member)
                .quantity(FIRST_ADD_VALUE)
                .build();

        cartRepository.save(cart);

        List<CartDto> cartDtos = cartRepository.findByMemberEquals(member).stream()
                .map(CartDto::CartToDto)
                .toList();

        return ApiResponse.success(cartDtos,
                String.format(CartSuccessMessage.ADD.getMessage(), cart.getProduct().getProductName())
        );
    }
    /**
     * 상품 수량 변경(직접입력)
     *
     * @param productId 상품 고유번호
     * @param memberId  사용자 고유번호
     * @return 장바구니 목록(CartDto)
     * @throws EntityNotFoundException  사용자 정보나 상품 정보를 찾을 수 없는 경우, 장바구니에 해당 상품이 존재하지 않는경우
     * @throws IllegalArgumentException "직접입력"한 값이 1보다 작은 경우
     */
    @Transactional
    public ApiResponse<List<CartDto>> updateQuantity(Long productId, Long count, Long memberId) {
        Cart cart = getCartByMemberIdAndProductId(memberId, productId);

        cart.updateQuantity(count);
        cartRepository.save(cart);

        List<CartDto> cartDtos = cartRepository.findByMemberEquals(cart.getMember()).stream()
                .map(CartDto::CartToDto)
                .toList();

        return ApiResponse.success(cartDtos,
                String.format(CartSuccessMessage.UPDATE.getMessage(), cart.getProduct().getProductName()));
    }
    /**
     * 상품 수량 변경(증가)
     *
     * @param productId 상품 고유번호
     * @param memberId  사용자 고유번호
     * @return 장바구니 목록(CartDto)
     * @throws EntityNotFoundException  사용자 정보나 상품 정보를 찾을 수 없는 경우, 장바구니에 해당 상품이 존재하지 않는경우
     */
    @Transactional
    public ApiResponse<List<CartDto>> plusQuantity(Long productId, Long memberId) {
        Cart cart = getCartByMemberIdAndProductId(memberId, productId);

        cart.plusQuantity();
        cartRepository.save(cart);

        List<CartDto> cartDtos = cartRepository.findByMemberEquals(cart.getMember()).stream()
                .map(CartDto::CartToDto)
                .toList();

        return ApiResponse.success(cartDtos,
                String.format(CartSuccessMessage.UPDATE.getMessage(), cart.getProduct().getProductName()));
    }
    /**
     * 상품 수량 변경(감소)
     *
     * @param productId 상품 고유번호
     * @param memberId  사용자 고유번호
     * @return 장바구니 목록(CartDto)
     * @throws EntityNotFoundException  사용자 정보나 상품 정보를 찾을 수 없는 경우, 장바구니에 해당 상품이 존재하지 않는경우
     * @throws IllegalArgumentException "-"한 값이 0이하인 경우
     */
    @Transactional
    public ApiResponse<List<CartDto>> minusQuantity(Long productId, Long memberId) {
        Cart cart = getCartByMemberIdAndProductId(memberId, productId);

        cart.minusQuantity();
        cartRepository.save(cart);

        List<CartDto> cartDtos = cartRepository.findByMemberEquals(cart.getMember()).stream()
                .map(CartDto::CartToDto)
                .toList();

        return ApiResponse.success(cartDtos,
                String.format(CartSuccessMessage.UPDATE.getMessage(), cart.getProduct().getProductName()));
    }
    /**
     * 장바구니 상품 삭제(개별)
     *
     * @param productId 상품 고유번호
     * @param memberId  사용자 고유번호
     * @return 장바구니 목록
     * @throws EntityNotFoundException 사용자 정보나 상품 정보를 찾을 수 없는 경우, 장바구니에 해당 상품이 존재하지 않는경우
     */
    @Transactional
    public ApiResponse<List<CartDto>> deleteProduct(Long productId, Long memberId) {
        Member member = getMemberByMemberId(memberId);
        Product product = getProductByProductId(productId);
        Cart cart = getCartByMemberAndProduct(member, product);

        cartRepository.delete(cart);

        List<CartDto> cartDtos = cartRepository.findByMemberEquals(member).stream()
                .map(CartDto::CartToDto)
                .toList();

        return ApiResponse.success(cartDtos,
                String.format(CartSuccessMessage.DELETE.getMessage(), cart.getProduct().getProductName()));
    }

    /**
     * 장바구니 상품 삭제 (전체)
     *
     * @param memberId 사용자 고유번호
     * @return 빈 장바구니 목록
     * @throws EntityNotFoundException 사용자 정보를 찾을 수 없는 경우
     */
    @Transactional
    public ApiResponse<List<CartDto>> deleteAllProduct(Long memberId) {
        Member member = getMemberByMemberId(memberId);
        cartRepository.deleteAllByMember(member);
        List<CartDto> cartDtos = cartRepository.findByMemberEquals(member).stream()
                .map(CartDto::CartToDto)
                .toList();
        return ApiResponse.success(cartDtos, CartSuccessMessage.DELETE_ALL.getMessage());
    }

    // member 검증 및 객체가져오는 메서드
    private Member getMemberByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(CartErrorMessage.NOT_FOUND_MEMBER.getMessage()));
    }

    // product 검증 및 객체가져오는 메서드
    private Product getProductByProductId(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(CartErrorMessage.NOT_FOUND_PRODUCT.getMessage()));
    }

    // Cart 검증 및 객체 가져오는 메서드
    private Cart getCartByMemberAndProduct(Member member, Product product) {
        return cartRepository.findByMemberEqualsAndProductEquals(member, product)
                .orElseThrow(
                        () -> new EntityNotFoundException(CartErrorMessage.NOT_EXIST_PRODUCT_IN_CART.getMessage()));
    }
    // member, product, cart를 검증 및 객체를 가져오는 메서드
    private Cart getCartByMemberIdAndProductId(Long memberId, Long productId){
        Member member = getMemberByMemberId(memberId);
        Product product = getProductByProductId(productId);
        Cart cart = getCartByMemberAndProduct(member, product);
        return cart;
    }
    // products 검증 메서드
    private void validateProducts(String productName, List<Product> products) {
        if (products.isEmpty()) {
            throw new NoContentFoundException(
                    String.format(CartErrorMessage.NOT_EXIST_PRODUCT.getMessage(), productName));
        }
    }
    // addProduct 시 이미 cart 에 존재하는 객체인지 검증하는 메서드
    private void checkAlreadyExist(Member member, Product product) {
        cartRepository.findByMemberEqualsAndProductEquals(member, product)
                .ifPresent(c -> {
                    throw new ConflictException(CartErrorMessage.ALREADY_EXIST_PRODUCT.getMessage());
                });
    }
}
