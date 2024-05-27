package project.back.etc.aboutlogin.apitestclass;

import lombok.Data;

import java.util.List;

@Data
public class Gptest {


    private String model;
    private List<SettingFor> messages;

    private float temperature;


    public Gptest(String model, List<SettingFor> messages, float temperature) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
    }
}

