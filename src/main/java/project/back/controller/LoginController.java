package project.back.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONUtil;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import project.back.dto.ApiResponse;
import project.back.dto.MemberDto;
import project.back.entity.Member;
import project.back.etc.aboutlogin.*;
import project.back.etc.aboutlogin.apitestclass.FriendDataDto;
import project.back.etc.aboutlogin.apitestclass.FriendDataObject;
import project.back.etc.aboutlogin.apitestclass.GptTEST;
import project.back.etc.aboutlogin.exception.TokenSending;
import project.back.repository.memberrepository.MemberRepository;
import project.back.service.memberservice.MemberService;
import reactor.core.publisher.Mono;
import reactor.netty.transport.ProxyProvider;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {


    private final JwtUtill jwtUtill;
    private final WebClient webClient;
    private final MemberService memberService;


    @Autowired
    private @Qualifier("redisTemplate") RedisTemplate<String,Object> redisTemplate;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoclientid;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakakoredirecturi;


    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String tokenuri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String userinfouri;




    @GetMapping("/reqlogin")
    @ResponseBody
    public ResponseEntity<ApiResponse<String>> logins(@RequestBody Access_code access_code) throws ParseException, IOException {
        String code=access_code.getAccess_code();
        MultiValueMap<String, String> accessTokenParam = accessTokenParams("authorization_code",kakaoclientid,code,kakakoredirecturi);
        //MultiValueMap<String, String> accessTokenRequest = accessTokenParam;
        String answerfromapi=webClient
                .mutate()
                .baseUrl(tokenuri)
                .defaultHeader("Content-type","application/x-www-form-urlencoded;charset=utf-8")
                .build()
                .post()
                .body(BodyInserters.fromFormData(accessTokenParam))
                .retrieve()
                .bodyToMono(String.class)
                .block();



        //받아온 데이터를 파싱하기. access_token을 받아오는 과정.
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(answerfromapi);

        String header = "Bearer " + jsonObject.get("access_token");
        System.out.println("header = " + header);
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", header);

        String userdata=webClient.mutate()
                .defaultHeader("Authorization",header)
                .build()
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .block();
        JSONObject profile=(JSONObject) jsonParser.parse(userdata);

        JSONObject properties = (JSONObject) profile.get("properties");




        JSONObject kakao_account = (JSONObject) profile.get("kakao_account");

        String email = (String) kakao_account.get("email");
        String userName = (String) properties.get("nickname");

        String user_profile_image=(String) properties.get("profile_image");



        //애같은 경우 이메일로 이미 가입한 멤버인지 체크 하는 과정.
        Optional<Member> member=memberService.finbdyemail(email);


        /*
        멤버가 있을경우 db에 다시저장하는 과정을 스킵하고 없으면 db 저장
        */
        List<Object> tokendata=gettokenandresponse(email,userName,member);


        /*

        * 레디스에 멤버 아이디로 저장되는 access_tokend이 존재시
        값을 지우고 다시저장 즉 최신화 없다면 최초저장.*/
        if(redisTemplate.opsForValue().get(String.format("member_kakao_token_%d",(Long)tokendata.get(1)))==null){

            redisTemplate.opsForValue().set(String.format("member_kakao_token_%d",(Long)tokendata.get(1)),jsonObject.get("access_token")
                    ,1000, TimeUnit.SECONDS);
        }
        else{
            redisTemplate.opsForValue().set(String.format("member_kakao_token_%d",(Long)tokendata.get(1)),jsonObject.get("access_token"));
        }


        ApiResponse.success((String)tokendata.get(0),"로그인 성공");
        return new ResponseEntity<>( ApiResponse.success((String)tokendata.get(0),"로그인 성공"), HttpStatus.OK);
    }


    public MultiValueMap<String, String> accessTokenParams(String grantType, String clientId,String code,String redirect_uri) {
        MultiValueMap<String, String> accessTokenParams = new LinkedMultiValueMap<>();
        accessTokenParams.add("grant_type", grantType);
        accessTokenParams.add("client_id", clientId);
        accessTokenParams.add("code", code);
        accessTokenParams.add("redirect_uri", redirect_uri);
        return accessTokenParams;
    }


    public List<Object> gettokenandresponse(String email, String username, Optional<Member> member) throws IOException{
        if(member.isPresent()){
            Member m=member.get();

            JwtToken jwtToken=jwtUtill.genjwt(username,m.getMemberId());

            List<Object> obj=new ArrayList<>();
            obj.add(jwtToken.getAccesstoken());
            obj.add(m.getMemberId());


            return obj;
        }
        else{

            Long id=memberService.memebersave(new MemberDto(email,username));


            JwtToken jwtToken=jwtUtill.genjwt(username,id);

            List<Object> obj=new ArrayList<>();
            obj.add(jwtToken.getAccesstoken());
            obj.add(id);

            return obj;
        }

    }


    @GetMapping("/logouts")
    @ResponseBody
    public ResponseEntity<ApiResponse<String>> logoutans(HttpServletRequest req){
        String token=req.getHeader("Authorization").substring(7);
        redisTemplate.delete(token);
        return new ResponseEntity<>(ApiResponse.success("null","로그아웃성공"),HttpStatus.OK);
    }


    @GetMapping("/returnfriendlist")
    public ResponseEntity<ApiResponse> friendlist(HttpServletRequest req){
        String token=req.getHeader("Authorization").substring(7);
        Long id=jwtUtill.getidfromtoken(token);
        String kakao_token=(String) redisTemplate.opsForValue().get(String.format("member_kakao_token_%d",id));


        FriendDataObject friend_data=webClient.mutate()
                .baseUrl("https://kapi.kakao.com/v1/api/talk/friends")
                .defaultHeader("Content-Type","application/x-www-form-urlencoded")
                .defaultHeader("Authorization",String.format("Bearer %s",kakao_token))
                .build()
                .get()
                .retrieve()
                .bodyToMono(FriendDataObject.class)
                .block();

        return new ResponseEntity<>(ApiResponse.success(friend_data,"친구데이터 전송"),HttpStatus.OK);

    }


    @GetMapping("/sendmsgtofriend")
    public void sendmsgfriend(@RequestBody FriendDataDto friendDataDto, HttpServletRequest req) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        String token=req.getHeader("Authorization").substring(7);
        Long id=jwtUtill.getidfromtoken(token);
        List<String> friend_uuid=friendDataDto.getFriend_uuid();
        String kakao_token=(String) redisTemplate.opsForValue().get(String.format("member_kakao_token_%d",id));
        /*String x=webClient.mutate()
                .baseUrl("https://kapi.kakao.com/v2/api/talk/memo/default/send")
                .defaultHeader("Content-Type","application/x-www-form-urlencoded")
                .defaultHeader("Authorization",String.format("Bearer %s",kakao_token))
                .build()
                .post()
                .body(BodyInserters.fromValue())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        */


    }
    @GetMapping("/kogpt")
    @ResponseBody
    public String gpt() throws JsonProcessingException, ParseException {
        GptTEST gptTEST=new GptTEST("주어진 음식들의 재료를 제공해주세요. \n\n 음식:새우 볶음밥 \n재료:새우,밥,식용유,당근,양파,계란,파\n\n 음식:자장면 \n재료:면,춘장,양파,파,돼지고기 앞다리살\n\n 음식:김치\n재료:소금,멸치 액젓,배추,고추가루,참기름,설탕\n\n 음식:알리오올리오\n재료:마늘,올리브오일,파스타 면,생크림,파마산치즈,후추,바질\n\n음식:된장 찌개\n재료:된장,두부,감자,호박,양파,청량고추,대파,다진 마늘\n\n 음식:새우 튀김\n재료:''" ,20L,1L,0.1f);
        ObjectMapper objectMapper=new ObjectMapper();
        //log.info("json변환꼴 :{}",objectMapper.writeValueAsString(gptTEST));
        //List<SettingFor> seeting=new ArrayList<>();
        //seeting.add(new SettingFor("user","Say this is a test!"));
        //Gptest gptest=new Gptest("gpt-3.5-turbo",seeting,0.7f);
        //log.info("gptest:{}",gptest);
        /*String data=webClient.mutate()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader("Authorization",)
                .defaultHeader("Content-Type","application/json")
                .build()
                .post()
                .body(BodyInserters.fromValue(objectMapper.writeValueAsString(gptest)))
                .retrieve()
                .bodyToMono(String.class)
                .block();*/




        String data=webClient.mutate()
                .baseUrl("https://api.kakaobrain.com/v1/inference/kogpt/generation")

                .defaultHeader("Content-Type","application/json")
                .build()
                .post()
                .body(BodyInserters.fromValue(objectMapper.writeValueAsString(gptTEST)))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        /*String data=webClient.mutate()
                .baseUrl("https://dapi.kakao.com/v2/local/search/address.json?query=서울")
                .defaultHeader("Authorization",String.format("KakaoAK %s",))
                .build()
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .block();*/

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(data);
        log.info("data:{}",data);
        return (String) ((JSONObject) ((JSONArray) jsonObject.get("generations")).get(0)).get("text");
        //return "ok";

    }






}




