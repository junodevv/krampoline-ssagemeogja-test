package project.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MartProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long martProductId;

    @Column(nullable = true)
    private Long stock;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = true)
    private String manufacturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "join_id")
    private JoinMart joinMart;

    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name="discount_id")
    private Discount discount;
}
