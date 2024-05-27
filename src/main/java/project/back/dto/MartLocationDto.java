package project.back.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
public class MartLocationDto {

    private double latitude;
    private double longitude;

    @Builder
    public MartLocationDto(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
