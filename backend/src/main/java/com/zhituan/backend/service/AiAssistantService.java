package com.zhituan.backend.service;

import com.zhituan.backend.dto.AiDtos;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AiAssistantService {
    AiDtos.AnalysisReportView analyzeContract(AiDtos.AnalyzeRequest request);

    AiDtos.AnalysisReportView detectBlackSlang(AiDtos.AnalyzeRequest request);

    AiDtos.AnalysisReportView evaluateJobRisk(AiDtos.AnalyzeRequest request);

    AiDtos.CozeQueryResponse queryCozeAgent(AiDtos.CozeQueryRequest request);

    AiDtos.CozeQueryResponse queryCozeAgent(AiDtos.CozeQueryRequest request, String userId);

    SseEmitter queryCozeAgentStream(AiDtos.CozeQueryRequest request);

    SseEmitter queryCozeAgentStream(AiDtos.CozeQueryRequest request, String userId);

    SseEmitter queryCozeAgentWithFileStream(String prompt, String sessionId, MultipartFile file);

    SseEmitter queryCozeAgentWithFileStream(String prompt, String sessionId, MultipartFile file, String userId);

    AiDtos.CozeFileQueryResponse queryCozeAgentWithFile(String prompt, String sessionId, MultipartFile file);

    AiDtos.CozeFileQueryResponse queryCozeAgentWithFile(String prompt, String sessionId, MultipartFile file, String userId);

    AiDtos.ChatHistoryView getChatHistory(String userId, String sessionId, Integer limit);

    List<AiDtos.AnalysisReportView> listHistory(String userId);
}
