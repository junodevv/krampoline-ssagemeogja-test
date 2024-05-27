package project.back.repository.memberrepository;

import project.back.dto.MemberDto;
import project.back.entity.Member;

import java.util.Optional;

public interface MemberRepositoryOther {


    Long membersave(MemberDto m);

    Optional<Member> findmemberbyemail(String email);
}
