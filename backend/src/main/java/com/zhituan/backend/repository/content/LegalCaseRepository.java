package com.zhituan.backend.repository.content;

import com.zhituan.backend.domain.model.content.LegalCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LegalCaseRepository extends JpaRepository<LegalCase, String> {
    List<LegalCase> findTop3ByOrderByCreatedAtDesc();
}
