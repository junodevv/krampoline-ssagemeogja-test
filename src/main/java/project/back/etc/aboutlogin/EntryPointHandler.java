package project.back.etc.aboutlogin;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Slf4j
@Component
public class EntryPointHandler implements AuthenticationEntryPoint {
    private HandlerExceptionResolver resolver;
    private MyExceptionHandler exceptionHandling;

    @Autowired
    public EntryPointHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver, MyExceptionHandler exceptionHandling) {
        this.resolver = resolver;
        this.exceptionHandling=exceptionHandling;
    }



    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("엔트리포인트 핸들러작동");
        if(null!=request.getAttribute("e")) {
            log.info("jwt관련에러");
            resolver.resolveException(request, response, null, (Exception) request.getAttribute("e"));
        }
        else{
            log.info("기타에러");
            resolver.resolveException(request, response, null, (Exception) new EtcError());
        }

    }
}