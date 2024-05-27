package project.back.dto;

import lombok.Data;

@Data
public class MemberDto {

    private String email;
    private String username;

    public MemberDto(String email, String username) {
        this.email = email;
        this.username = username;
    }
}
