package project.back.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
public class ReviewDto {
    private String reviewContent;
    private BigDecimal score;
    private String memberName;
    private Long memberId; // 리뷰를 작성한 회원의 ID
    private Long martId; // 리뷰 대상인 마트의 ID

    @Builder

    public ReviewDto(String reviewContent, BigDecimal score, String memberName, Long memberId, Long martId) {
        this.reviewContent = reviewContent;
        this.score = score;
        this.memberName = memberName;
        this.memberId = memberId;
        this.martId = martId;
    }
}