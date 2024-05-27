package project.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinMart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long joinId;

    @Column(nullable = false)
    private String store;
}
