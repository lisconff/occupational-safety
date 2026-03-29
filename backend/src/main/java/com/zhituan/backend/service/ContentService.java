package com.zhituan.backend.service;

import com.zhituan.backend.domain.model.content.LegalCase;

import java.util.List;

public interface ContentService {
    List<LegalCase> listHomepageFeaturedCases();

    LegalCase getCaseDetail(String caseId);
}
