package project.back.repository.memberrepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.back.dto.MemberDto;
import project.back.entity.Member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryOtherImpl implements MemberRepositoryOther {


    private final EntityManager em;



    @Override
    public Optional<Member> findmemberbyemail(String email){
        String query="select m from Member m where m.email=:email";
        List<Member> member=em.createQuery(query,Member.class)
                .setParameter("email",email)
                .getResultList();

        Optional<Member> optmember=Optional.ofNullable(member.get(0));



        return optmember;

    }

    @Override
    public Long membersave(MemberDto m) {
        Member member=new Member(m.getEmail(),m.getUsername());
        member.setCreate_time(LocalDateTime.now());

        em.persist(member);
        return member.getMemberId();
    }
}
