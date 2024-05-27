package project.back.etc;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.back.etc.aboutlogin.JwtUtill;

@Component
@RequiredArgsConstructor
public class RequestMemberMapper {

    private final JwtUtill jwtUtill;

    public Long RequestToMemberId(HttpServletRequest request){
        String accessToken = request.getHeader("Authorization").substring(7);
        Long memberId = jwtUtill.getidfromtoken(accessToken);
        return memberId;
    }

}
