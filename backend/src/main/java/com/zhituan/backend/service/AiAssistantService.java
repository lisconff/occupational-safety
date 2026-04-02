package com.zhituan.backend.service;

import com.zhituan.backend.dto.AiDtos;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AiAssistantService {
    AiDtos.AnalysisReportView analyzeContract(AiDtos.AnalyzeRequest request);

    AiDtos.AnalysisReportView detectBlackSlang(AiDtos.AnalyzeRequest request);

    AiDtos.AnalysisReportView evaluateJobRisk(AiDtos.AnalyzeRequest request);

    AiDtos.CozeQueryResponse queryCozeAgent(AiDtos.CozeQueryRequest request);

    AiDtos.CozeFileQueryResponse queryCozeAgentWithFile(String prompt, String sessionId, MultipartFile file);

    List<AiDtos.AnalysisReportView> listHistory(String userId);
}
