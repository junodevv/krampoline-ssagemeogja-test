package project.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.back.configuration.LoginUser;
import project.back.dto.ApiResponse;
import project.back.dto.DiscountInfoDto;
import project.back.service.MartProductService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/marts/selling")
public class MartProductController {

    private final MartProductService martProductService;

    /** 장바구니에 있는 모든 상품이 존재하는 마트 목록 + 마트별 합계 출력 */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<Long, Long>>> getMartsForCart(
            @LoginUser Long memberId
    ) {
        ApiResponse<Map<Long, Long>> productTotalPrices =
                martProductService.findMartsByProductIds(memberId);
        return ResponseEntity.ok(productTotalPrices);
    }

    /** 마트별 상품 가격 세부사항 조회 */
    @GetMapping("/{martId}")
    public ResponseEntity<ApiResponse<List<DiscountInfoDto>>> getMartInfoForMartId(
            @PathVariable("martId") Long martId,
            @LoginUser Long memberId
    ) {
        ApiResponse<List<DiscountInfoDto>> martInfo =
                martProductService.findMartInfoByMartId(martId, memberId);
        return ResponseEntity.ok(martInfo);
    }
}
