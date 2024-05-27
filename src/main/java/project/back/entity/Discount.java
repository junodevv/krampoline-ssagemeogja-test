package project.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long discountId;

    @Column(precision = 3, scale = 1, nullable = false)
    private BigDecimal discountRate;

    /* 더미 데이터 삽입에 사용 */
    public Discount(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }
}
