package project.back.repository.memberrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.back.entity.Member;

public interface MemberRepository extends JpaRepository<Member,Long>, MemberRepositoryOther {
}
