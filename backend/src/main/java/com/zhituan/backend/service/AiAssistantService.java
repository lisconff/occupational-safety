package com.zhituan.backend.service;

import com.zhituan.backend.dto.AiDtos;

import java.util.List;

public interface AiAssistantService {
    AiDtos.AnalysisReportView analyzeContract(AiDtos.AnalyzeRequest request);

    AiDtos.AnalysisReportView detectBlackSlang(AiDtos.AnalyzeRequest request);

    AiDtos.AnalysisReportView evaluateJobRisk(AiDtos.AnalyzeRequest request);

    List<AiDtos.AnalysisReportView> listHistory(String userId);
}
