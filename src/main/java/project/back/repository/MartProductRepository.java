package project.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.back.dto.DiscountInfoDto;
import project.back.entity.JoinMart;
import project.back.entity.MartProduct;
import project.back.entity.Product;

import java.util.List;

@Repository
public interface MartProductRepository extends JpaRepository<MartProduct, Long> {

    @Query("""
            SELECT new project.back.dto.DiscountInfoDto(
                mp.product.productId,
                mp.product.productName,
                mp.joinMart.joinId,
                mp.stock,
                mp.price,
                d.discountRate)
            FROM MartProduct mp
            LEFT JOIN mp.discount d
            WHERE mp.product IN :products AND mp.joinMart IN :joinMarts
            """)
    List<DiscountInfoDto> findDiscountsByProductsAndJoinMarts(List<Product> products, List<JoinMart> joinMarts);
}
