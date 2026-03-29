package com.zhituan.backend.service.impl;

import com.zhituan.backend.domain.model.simulation.GameScene;
import com.zhituan.backend.domain.model.simulation.SimulationGame;
import com.zhituan.backend.repository.simulation.GameSceneRepository;
import com.zhituan.backend.repository.simulation.SimulationGameRepository;
import com.zhituan.backend.service.SimulationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimulationServiceImpl implements SimulationService {

    private final SimulationGameRepository simulationGameRepository;
    private final GameSceneRepository gameSceneRepository;

    public SimulationServiceImpl(SimulationGameRepository simulationGameRepository, GameSceneRepository gameSceneRepository) {
        this.simulationGameRepository = simulationGameRepository;
        this.gameSceneRepository = gameSceneRepository;
    }

    @Override
    public SimulationGame getGameEntry(String gameId) {
        return simulationGameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("游戏入口不存在"));
    }

    @Override
    public List<GameScene> listScenes(String gameId) {
        return gameSceneRepository.findByGameIdOrderBySwitchOrderAsc(gameId);
    }
}
