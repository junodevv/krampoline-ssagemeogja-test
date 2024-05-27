package project.back.service.reviewService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.back.dto.ReviewDto;
import project.back.entity.Mart;
import project.back.entity.Member;
import project.back.entity.Review;
import project.back.repository.MartRepository;
import project.back.repository.memberrepository.MemberRepository;
import project.back.repository.reviewrepository.ReviewRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final MartRepository martRepository;

    //review 조회 서비스
    public List<ReviewDto> getReviewByMartId(Long martId){
        List<ReviewDto> reviewDtos = new ArrayList<>();

        List<Review> reviews = reviewRepository.findByMart_Id(martId); //리뷰 없을시 빈 리스트 반
        for (Review review : reviews) {
            ReviewDto reviewDto = ReviewDto.builder()
                    .reviewContent(review.getReviewContent())
                    .memberName(review.getMember().getName())
                    .score(review.getScore())
                    .build();
            reviewDtos.add(reviewDto);
        }
        return reviewDtos;
    }

    //review + 평점 작성하기 서비스
    public Review writeReview(ReviewDto reviewDto){
        Member member = memberRepository.findById(reviewDto.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found "));

        Mart mart = martRepository.findById(reviewDto.getMartId())
                .orElseThrow(() -> new EntityNotFoundException("Mart not found "));

        Review review = Review.builder()
                .reviewContent(reviewDto.getReviewContent())
                .score(reviewDto.getScore())
                .member(member)
                .mart(mart)
                .build();

        return reviewRepository.save(review);
    }

    //리뷰 + 평점 삭제하기 서비스
    public void deleteReview(Long reviewId){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()->new EntityNotFoundException("Review not found"));
        reviewRepository.delete(review);
    }

    //리뷰 + 평점 수정하기 서비스
    public ReviewDto updateReview(Long reviewId, String content, BigDecimal score){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new EntityNotFoundException("review not find"));

        // Builder를 사용하여 기존 리뷰의 내용과 평점을 수정
        Review updatedReview = Review.builder()
                .reviewId(reviewId) // 기존 리뷰의 ID를 유지
                .reviewContent(content != null ? content : review.getReviewContent()) // content가 null이 아닌 경우에만 새로운 리뷰 내용을 설정
                .score(score != null ? score : review.getScore()) // score가 null이면 기존 리뷰의 score를 유지하도록 설정
                .member(review.getMember()) // 기존 리뷰와 동일한 회원 정보 사용
                .mart(review.getMart()) // 기존 리뷰와 동일한 마트 정보 사용
                .build();

        // 수정된 리뷰를 저장하고, 저장된 리뷰를 리턴
        Review savedReview = reviewRepository.save(updatedReview);
        return ReviewDto.builder()
                .score(savedReview.getScore())
                .reviewContent(savedReview.getReviewContent())
                .build();
    }
}
