package com.zhituan.backend.domain.model.simulation;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "simulation_games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulationGame {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String gameId;

    private String title;
    private String description;
    private String entryType;
    private Boolean isEnabled;
    private String defaultSceneId;
    private LocalDateTime createdAt;
}
