package project.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.back.entity.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
