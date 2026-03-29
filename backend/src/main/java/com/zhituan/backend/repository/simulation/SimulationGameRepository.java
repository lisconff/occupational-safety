package com.zhituan.backend.repository.simulation;

import com.zhituan.backend.domain.model.simulation.SimulationGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimulationGameRepository extends JpaRepository<SimulationGame, String> {
}
