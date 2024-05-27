package project.back.service.memberservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.back.dto.MemberDto;
import project.back.entity.Member;
import project.back.repository.memberrepository.MemberRepository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class MemberService {


    private MemberRepository memberRepository;

    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    public MemberService(@Qualifier("redisTemplate") RedisTemplate<String,Object> redisTemplate,MemberRepository memberRepository) {
        this.redisTemplate =redisTemplate;
        this.memberRepository=memberRepository;
    }



    public Long memebersave(MemberDto member){
       return memberRepository.membersave(member);


    }

    public Optional<Member> finbdyemail(String email){


        if(redisTemplate.opsForHash().get("member",email)==null){
            Optional<Member> member=memberRepository.findmemberbyemail(email);


            redisTemplate.opsForHash().put("member",email,member);
            redisTemplate.expire("member",300,TimeUnit.SECONDS);

            //redisTemplate.opsForValue().set(id.toString(),member,500, TimeUnit.SECONDS);

            return member;
        }

        Optional<Member>member=Optional.ofNullable((Member)redisTemplate.opsForHash().get("member",email));
        return member;

    }



    public Optional<Member> findmember(Long id) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        if(redisTemplate.opsForHash().get("member",id.toString())==null){
            Optional<Member> member=memberRepository.findById(id);


            redisTemplate.opsForHash().put("member",id.toString(),objectMapper.writeValueAsString(member.get()));
            redisTemplate.expire("member",300,TimeUnit.SECONDS);

            //redisTemplate.opsForValue().set(id.toString(),member,500, TimeUnit.SECONDS);

            return member;
        }

        Optional<Member>member=Optional.ofNullable(objectMapper.readValue((String) redisTemplate.opsForHash().get("member",id.toString()),Member.class));

        return member;

    }


}
