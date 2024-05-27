package project.back.etc.aboutlogin;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import project.back.service.memberservice.MemberService;

import java.io.IOException;

@Slf4j
@Component
public class JwtFilter extends GenericFilterBean {


    @Autowired
    private JwtUtill jwtUtill;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private MemberService memberService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        String token=resolvetoken((HttpServletRequest) servletRequest);
        log.info("-----------------filter start--------------");
        log.info("---------------req uri:{}",((HttpServletRequest) servletRequest).getRequestURI());
        log.info("filter-token:{}",token);
        try{if(token!=null&&jwtUtill.validatetoken(token)){
            log.info("--------------success in jwt filter---------");

            Long id=jwtUtill.getidfromtoken(token);


            Authentication authentication= jwtUtill.getauth(token);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        }
        catch(Exception e){
            log.info("-----------error in jwtfilter---------------:{}",e.getClass());
            servletRequest.setAttribute("e",e);
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }

    public String resolvetoken(HttpServletRequest req){
        String token=req.getHeader("Authorization");
        if(StringUtils.hasText(token)&&token.startsWith("Bearer")&&token.length()>7){
            return token.substring(7);
        }
        return null;
    }
}