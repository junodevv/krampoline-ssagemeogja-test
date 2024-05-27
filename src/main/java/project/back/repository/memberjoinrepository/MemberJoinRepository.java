package project.back.repository.memberjoinrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.back.entity.Member;

@Repository
public interface MemberJoinRepository extends JpaRepository<Member,Long> {
}
