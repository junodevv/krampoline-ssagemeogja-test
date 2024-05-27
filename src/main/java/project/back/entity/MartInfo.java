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
public class MartInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dataId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String salePrice;

    @Column(nullable = false)
    private String store;

    @Column(nullable = true)
    private String manufacturer;
}
