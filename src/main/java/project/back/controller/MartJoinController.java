package project.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import project.back.configuration.LoginUser;
import project.back.dto.ApiResponse;
import project.back.dto.MartLocationDto;
import project.back.dto.MartResponseDto;
import project.back.service.martjoinservice.MartJoinService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MartJoinController {

    private final MartJoinService martJoinService;

    //주소 끌고 와서 위도 경도 api를 통해서 받아온다.
    @GetMapping("/marts")
    public ResponseEntity<ApiResponse<List<MartResponseDto>>> getMartPlace(@LoginUser Long memberId) {
        String address = martJoinService.findAddress(memberId);
        MartLocationDto martLocationDto = martJoinService.findLatitudeLongitude(address);
        ApiResponse<List<MartResponseDto>> martResponses = martJoinService.searchMarts(martLocationDto.getLatitude(), martLocationDto.getLongitude(), 20000);
        return ResponseEntity.ok(martResponses);
    }
}