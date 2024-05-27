package project.back.etc.aboutlogin.apitestclass;
import lombok.Data;

@Data
public class GptTEST {

    private String prompt;
    private Long max_tokens;
    private Long n;
    private float temperature;

    public GptTEST(String prompt,Long max_tokens, Long n,float temperature) {
        this.prompt = prompt;
        this.max_tokens = max_tokens;
        this.n = n;
        this.temperature=temperature;
    }
}
