package project.back.etc.aboutlogin.apitestclass;


import lombok.Data;

@Data
public class SettingFor {

    private String role;
    private String content;


    public SettingFor(String role, String content) {
        this.role = role;
        this.content = content;
    }
}

