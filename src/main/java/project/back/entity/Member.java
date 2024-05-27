package project.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Member extends UpdateCreateTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;


    @Column(name="email")
    private String email;


    @Column(name="name")
    private String name;


    @Column(name="address")
    private String address;

    public Member(String email, String name) {
        this.email = email;
        this.name = name;
    }
}
