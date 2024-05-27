package project.back.service.martjoinservice;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import project.back.dto.ApiResponse;
import project.back.dto.MartJoinContentDto;
import project.back.dto.MartLocationDto;
import project.back.dto.MartResponseDto;
import project.back.entity.JoinMart;
import project.back.entity.Mart;
import project.back.entity.Member;
import project.back.etc.martproduct.MartAndProductMessage;
import project.back.repository.MartRepository;
import project.back.repository.memberjoinrepository.MemberJoinRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MartJoinService {

    @Value("${kakao.rest.api.key}")
    private String restApiKey;

    private final MemberJoinRepository memberJoinRepository;
    private final RestTemplate restTemplate;
    private final MartRepository martRepository;

    //memberId를 통해 주소 찾아내기
    public String findAddress(Long memberId){
        Optional<Member> memberOptional = memberJoinRepository.findById(memberId);

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            return member.getAddress();
        } else {
            throw new IllegalArgumentException(MartAndProductMessage.NOT_FOUND_MEMBER.getMessage());
        }
    }

    //주소를 받아서 카카오api 사용해서  위도와 경도 찾아내는 것
    public MartLocationDto findLatitudeLongitude(String address){
        String url = "https://dapi.kakao.com/v2/local/search/address.json" + "?query=" + address;

        //헤더 만들기
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "KakaoAK " + restApiKey);

        //엔티티 만들기
        HttpEntity<String> entity = new HttpEntity<>(headers);

        //restTemplate 이용
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        // 응답 처리
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = responseEntity.getBody();
            if (responseBody != null && responseBody.containsKey("documents")) {
                List<Map<String, Object>> documents = (List<Map<String, Object>>) responseBody.get("documents");
                if (!documents.isEmpty()) {
                    Map<String, Object> document = documents.get(0);
                    Map<String, Object> addressInfo = (Map<String, Object>) document.get("address");
                    double latitude = Double.parseDouble((String)addressInfo.get("y")) ;
                    double longitude =  Double.parseDouble((String) addressInfo.get("x"));

                    MartLocationDto martLocationDto = MartLocationDto.builder()
                            .latitude(latitude)
                            .longitude(longitude)
                            .build();
                    return martLocationDto;
                }
            }
        }
        return null;
    }

    //위도와 경도를 기준으로 전방 200m(임의)안에 마트조인 하는 부분
    public ApiResponse<List<MartResponseDto>> searchMarts(double latitude, double longitude, int radius) {
        String url = "https://dapi.kakao.com/v2/local/search/category.json" +
                "?category_group_code=MT1" +       // 여기ㅣ 부분을 카테고리 바꾸면 된다.
                "&radius=" + radius +
                "&x=" + longitude +
                "&y=" + latitude +
                "&sort=distance";  // 거리별로 정렬 기능

        //헤더 만들기
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "KakaoAK " + restApiKey);

        //엔티티 만들기  (headers) 로 헤더 정보 안에 넣고 http 응답 요청을 보낼떄 필요한 entity를 만드는것
        HttpEntity<String> entity = new HttpEntity<>(headers);

        //restTemplate 이용해서
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = responseEntity.getBody();
            if (responseBody != null && responseBody.containsKey("documents")) {
                List<Map<String, Object>> documents = (List<Map<String, Object>>) responseBody.get("documents");
                List<MartResponseDto> martResponses = documents.stream()
                        .map(this::mapToDto)
                        .map(this::saveMart)
                        .toList();
                return ApiResponse.success(martResponses, MartAndProductMessage.LOADED_MART.getMessage());
            }
        }
        return ApiResponse.fail(MartAndProductMessage.NOT_FOUND_MART.getMessage());
    }

    /** 지도 API에서 받은 데이터를 MartJoinContentDto 에 담아서 반환 */
    private MartJoinContentDto mapToDto(Map<String, Object> document) {
        return MartJoinContentDto.builder()
                .id((String) document.get("id"))
                .distance((String) document.get("distance"))
                .placeName((String) document.get("place_name"))
                .address((String) document.get("address_name"))
                .roadAddress((String) document.get("road_address_name"))
                .phone((String) document.get("phone"))
                .build();
    }

    /**
     * 검색된 마트 정보를 저장 및 JoinMart에 해당 마트가 있다면 연결
     *
     * @param contentDto 저장할 마트 정보
     * @return MartResponseDto로 저장된 마트 정보 반환
     */
    public MartResponseDto saveMart(MartJoinContentDto contentDto) {
        JoinMart joinMart = martRepository.findJoinMartByNameContaining(contentDto.getPlaceName()).orElse(null);
        Mart mart = martRepository.save(Mart.builder()
                .martName(contentDto.getPlaceName())
                .martAddress(contentDto.getRoadAddress())
                .joinMart(joinMart)
                .build());
        return new MartResponseDto(mart.getMartName(), mart.getMartAddress());
    }
}
