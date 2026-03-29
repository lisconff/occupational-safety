package com.zhituan.backend.domain.model.simulation;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_scenes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameScene {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String sceneId;

    private String gameId;
    private String sceneName;
    private String sceneType;

    @Column(length = 2000)
    private String sceneDescription;

    private Integer switchOrder;
    private Boolean isLocked;
    private String passCondition;
    private String resourceUrl;
}
