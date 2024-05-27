package project.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public class UpdateCreateTime {


    @Column(name="create_time")
    private LocalDateTime create_time;


    @Column(name="update_time")
    private LocalDateTime update_time;


}
