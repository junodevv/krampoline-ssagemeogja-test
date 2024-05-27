package project.back.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "review_content")
    private String reviewContent;

    @Column(name = "score", precision = 2, scale = 1, nullable = false)
    private BigDecimal score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="mart_id")
    private Mart mart;


    @Builder
    public Review(Long reviewId,String reviewContent, BigDecimal score, Member member, Mart mart) {
        this.reviewId = reviewId;
        this.reviewContent = reviewContent;
        this.score = score;
        this.member = member;
        this.mart = mart;
    }
}
