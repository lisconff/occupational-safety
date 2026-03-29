package com.zhituan.backend.repository.ai;

import com.zhituan.backend.domain.model.ai.AnalysisReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisReportRepository extends JpaRepository<AnalysisReport, String> {
    List<AnalysisReport> findByUserIdOrderByCreatedAtDesc(String userId);
}
