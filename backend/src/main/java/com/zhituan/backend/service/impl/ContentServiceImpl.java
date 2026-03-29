package com.zhituan.backend.service.impl;

import com.zhituan.backend.domain.model.content.LegalCase;
import com.zhituan.backend.repository.content.LegalCaseRepository;
import com.zhituan.backend.service.ContentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

    private final LegalCaseRepository legalCaseRepository;

    public ContentServiceImpl(LegalCaseRepository legalCaseRepository) {
        this.legalCaseRepository = legalCaseRepository;
    }

    @Override
    public List<LegalCase> listHomepageFeaturedCases() {
        return legalCaseRepository.findTop3ByOrderByCreatedAtDesc();
    }

    @Override
    public LegalCase getCaseDetail(String caseId) {
        return legalCaseRepository.findById(caseId)
                .orElseThrow(() -> new IllegalArgumentException("案例不存在"));
    }
}
