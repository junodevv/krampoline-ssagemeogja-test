package project.back.etc.aboutlogin;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import project.back.etc.aboutlogin.JwtToken;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Component
@Slf4j
public class JwtUtill {

    @Value("${jwt.secret}")
    private  String key;

    @Value("${jwt.expiration}")
    private  Long expiration;

    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    public JwtUtill(@Qualifier("redisTemplate") RedisTemplate<String,Object> redisTemplate) {
        this.redisTemplate =redisTemplate;
    }

    public JwtToken genjwt(String username, Long id){
        //log.info("genjwt");
        //log.info("redsitemplate:{}---{}",redisTemplate,redisTemplate.getClientList());
        Random random=new Random();
        int int_value=random.ints().limit(3).sum();

        //String claim=auth.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        String claim="ROLE_user";
        String accesstoken= Jwts.builder()
                .claim("auth",claim)
                .claim("user_id",id)
                .claim("randkey",int_value)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expiration+300000))
                .signWith(SignatureAlgorithm.HS256,key)
                .compact();

        String refreshtoken=Jwts
                .builder()
                .setSubject(username)
                .claim("user_id",id)
                .claim("randkey",int_value)
                .setExpiration(new Date(System.currentTimeMillis()+300000))
                .signWith(SignatureAlgorithm.HS256,key)
                .compact();

        redisTemplate.opsForValue().set(accesstoken,refreshtoken,300,TimeUnit.SECONDS);
        return JwtToken.builder()
                .accesstoken(accesstoken)
                .refreshtoken(refreshtoken)
                .grantType("Bearer")
                .build();


    }


    public String regenaccesstoken(String username,Long id){

        Random random=new Random();
        int int_value=random.ints().limit(3).sum();

        //String claim=auth.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        String claim="ROLE_user";
        String accesstokne= Jwts.builder()
                .claim("auth",claim)
                .claim("user_id",id)
                .claim("randkey",int_value)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expiration+1))
                .signWith(SignatureAlgorithm.HS256,key)
                .compact();



        return accesstokne;

    }







   /*public JwtToken genjwt2(Collection< ? extends GrantedAuthority> auth, String username, Long id){

        Random random=new Random();
        int int_value=random.ints().limit(3).sum();

        String claim=auth.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        String accesstokne= Jwts.builder()
                .claim("auth",claim)
                .claim("user_id",id)
                .claim("randkey",int_value)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expiration+60000))
                .signWith(SignatureAlgorithm.HS256,key)
                .compact();

        String refreshtoken=Jwts
                .builder()
                .setSubject(username)
                .claim("user_id",id)
                .claim("randkey",int_value)
                .setExpiration(new Date(System.currentTimeMillis()+60000))
                .signWith(SignatureAlgorithm.HS256,key)
                .compact();
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(accesstokne,refreshtoken,60, TimeUnit.SECONDS);
        return JwtToken.builder()
                .accesstoken(accesstokne)
                .refreshtoken(refreshtoken)
                .grantType("Bearer")
                .build();


    }*/
    /*public Authentication getauthforrefresh(String token) {
        Claims claims = getclaims(token);

        List<SimpleGrantedAuthority> auth=new ArrayList<>();
        SimpleGrantedAuthority simpleGrantedAuthority=new SimpleGrantedAuthority("ROLE_user");
        auth.add(simpleGrantedAuthority);
        String username=claims.getSubject();

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
                new UsernamePasswordAuthenticationToken(username,"",auth);


        return usernamePasswordAuthenticationToken;

    }*/


    public List<Object> getdatafromtoken(String token){
        Claims claims=getclaims(token);
        log.info("datatype:{}",claims.get("user_id").getClass());
        Integer x=(Integer)claims.get("user_id");
        //String auth=(String)claims.get("auth");
        String username=claims.getSubject();
        List<Object> datalist=new ArrayList<>();
        datalist.add(x.longValue());
        datalist.add(username);
        return datalist;
    }


    public Long getidfromtoken(String token){
        Claims claims=getclaims(token);
        Integer x=(Integer)claims.get("user_id");
        return x.longValue();
    }


    public Authentication getauth(String token) {
        Claims claims = getclaims(token);

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth")
                        .toString().split(","))
                .map(x -> new SimpleGrantedAuthority(x))
                .collect(Collectors.toList());
        String username=claims.getSubject();

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
                new UsernamePasswordAuthenticationToken(username,"",authorities);


        return usernamePasswordAuthenticationToken;

    }

    public boolean validatetoken(String token)throws SecurityException, MalformedJwtException, ExpiredJwtException,UnsupportedJwtException,IllegalArgumentException{

        Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        return true;

    }


    public Claims getclaims(String token)throws SecurityException, MalformedJwtException, ExpiredJwtException,UnsupportedJwtException,IllegalArgumentException{

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

    }
}
