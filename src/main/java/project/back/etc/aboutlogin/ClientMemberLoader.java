package project.back.etc.aboutlogin;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import project.back.entity.Member;

@Component
public class ClientMemberLoader {
    public static Member getClientMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            return  (Member)authentication.getPrincipal();
    }
}