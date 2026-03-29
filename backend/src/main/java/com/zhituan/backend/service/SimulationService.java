package com.zhituan.backend.service;

import com.zhituan.backend.domain.model.simulation.GameScene;
import com.zhituan.backend.domain.model.simulation.SimulationGame;

import java.util.List;

public interface SimulationService {
    SimulationGame getGameEntry(String gameId);

    List<GameScene> listScenes(String gameId);
}
