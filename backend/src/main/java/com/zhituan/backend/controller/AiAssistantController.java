package com.zhituan.backend.controller;

import com.zhituan.backend.common.api.ApiResponse;
import com.zhituan.backend.dto.AiDtos;
import com.zhituan.backend.service.AiAssistantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiAssistantController {

    private final AiAssistantService aiAssistantService;

    public AiAssistantController(AiAssistantService aiAssistantService) {
        this.aiAssistantService = aiAssistantService;
    }

    @PostMapping("/analyze/contract")
    public ApiResponse<AiDtos.AnalysisReportView> analyzeContract(@Valid @RequestBody AiDtos.AnalyzeRequest request) {
        return ApiResponse.ok(aiAssistantService.analyzeContract(request));
    }

    @PostMapping("/analyze/black-slang")
    public ApiResponse<AiDtos.AnalysisReportView> detectBlackSlang(@Valid @RequestBody AiDtos.AnalyzeRequest request) {
        return ApiResponse.ok(aiAssistantService.detectBlackSlang(request));
    }

    @PostMapping("/analyze/job-risk")
    public ApiResponse<AiDtos.AnalysisReportView> evaluateJobRisk(@Valid @RequestBody AiDtos.AnalyzeRequest request) {
        return ApiResponse.ok(aiAssistantService.evaluateJobRisk(request));
    }

    @GetMapping("/reports/{userId}")
    public ApiResponse<List<AiDtos.AnalysisReportView>> listHistory(@PathVariable String userId) {
        return ApiResponse.ok(aiAssistantService.listHistory(userId));
    }
}
