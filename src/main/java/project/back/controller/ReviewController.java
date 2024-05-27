package project.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.back.dto.ReviewDto;
import project.back.entity.Review;
import project.back.service.reviewService.ReviewService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    //리뷰  + 평점 조회 get
    @GetMapping("/{mart_id}")
    public ResponseEntity<List<ReviewDto>> getReview(@PathVariable Long mart_id){
        List<ReviewDto> reviews = reviewService.getReviewByMartId(mart_id);
        if (reviews.isEmpty()) {
            return ResponseEntity.noContent().build(); // 리뷰가 없는 경우 204 No Content 반환
        }
        return ResponseEntity.ok(reviews); // 리뷰가 있는 경우 리뷰 리스트 반환
    }

    //리뷰 + 평점 작성 post
    @PostMapping("/")
    public ResponseEntity<Review> writeReview(@RequestBody ReviewDto reviewDto){
        Review review = reviewService.writeReview(reviewDto);
        return ResponseEntity.ok(review);
    }

    //리뷰 + 평점 삭제 delete
    @DeleteMapping("/{review_id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long review_id){
        reviewService.deleteReview(review_id);
        return ResponseEntity.noContent().build(); //삭제 성공시  204 no content 반
    }

    //리뷰 + 평점 수정 patch
    @PatchMapping("/{review_id}")
    public ResponseEntity<ReviewDto> updateReview( @PathVariable Long review_id,@RequestBody ReviewDto reviewDto) {
        ReviewDto updatedReview = reviewService.updateReview(review_id, reviewDto.getReviewContent(), reviewDto.getScore());
        return ResponseEntity.ok(updatedReview);
    }
}
