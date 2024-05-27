package project.back.etc.aboutlogin.exception;

import lombok.Data;

@Data
public class TokenSending {
    private String access_token;


    public TokenSending() {
    }

    public TokenSending(String access_token) {
        this.access_token = access_token;
    }
}
