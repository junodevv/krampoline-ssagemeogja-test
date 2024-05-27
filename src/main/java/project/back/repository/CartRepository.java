package project.back.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.back.dto.CartProductDto;
import project.back.entity.Cart;
import project.back.entity.Member;

import java.util.List;
import project.back.entity.Member;
import project.back.entity.Product;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("""
             SELECT new project.back.dto.CartProductDto(c.quantity, c.member.memberId, c.product.productId)
             FROM Cart c
             WHERE c.member = :member
             """)
    List<CartProductDto> findCartsByMember(Member member);

    Optional<Cart> findByMemberEqualsAndProductEquals(Member member, Product product);

    List<Cart> findByMemberEquals(Member member);

    void deleteAllByMember(Member member);
}
