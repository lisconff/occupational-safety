package com.zhituan.backend.repository.ai;

import com.zhituan.backend.domain.model.ai.RiskItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskItemRepository extends JpaRepository<RiskItem, String> {
    List<RiskItem> findByReportId(String reportId);
}
