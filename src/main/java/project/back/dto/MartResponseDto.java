package project.back.dto;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MartResponseDto {
    @Column(nullable = false)
    private String martName;

    @Column(nullable = false)
    private String address;

    public MartResponseDto(String martName, String address) {
        this.martName = martName;
        this.address = address;
    }
}
