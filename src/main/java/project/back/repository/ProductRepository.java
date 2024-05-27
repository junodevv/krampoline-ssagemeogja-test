package project.back.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import project.back.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByProductNameContaining(String productName);
}
