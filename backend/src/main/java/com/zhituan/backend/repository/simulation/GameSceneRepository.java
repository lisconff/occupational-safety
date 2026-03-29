package com.zhituan.backend.repository.simulation;

import com.zhituan.backend.domain.model.simulation.GameScene;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameSceneRepository extends JpaRepository<GameScene, String> {
    List<GameScene> findByGameIdOrderBySwitchOrderAsc(String gameId);
}
