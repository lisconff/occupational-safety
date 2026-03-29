package com.zhituan.backend.controller;

import com.zhituan.backend.common.api.ApiResponse;
import com.zhituan.backend.domain.model.simulation.GameScene;
import com.zhituan.backend.domain.model.simulation.SimulationGame;
import com.zhituan.backend.service.SimulationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @GetMapping("/games/{gameId}")
    public ApiResponse<SimulationGame> getGameEntry(@PathVariable String gameId) {
        return ApiResponse.ok(simulationService.getGameEntry(gameId));
    }

    @GetMapping("/games/{gameId}/scenes")
    public ApiResponse<List<GameScene>> listScenes(@PathVariable String gameId) {
        return ApiResponse.ok(simulationService.listScenes(gameId));
    }
}
