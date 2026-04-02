package com.zhituan.backend.controller;

import com.zhituan.backend.common.api.ApiResponse;
import com.zhituan.backend.dto.AiDtos;
import com.zhituan.backend.service.AiAssistantService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
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

    @PostMapping("/coze/query")
    public ApiResponse<AiDtos.CozeQueryResponse> queryCozeAgent(@Valid @RequestBody AiDtos.CozeQueryRequest request) {
        return ApiResponse.ok(aiAssistantService.queryCozeAgent(request));
    }

    @PostMapping(value = "/coze/query-with-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AiDtos.CozeFileQueryResponse> queryCozeAgentWithFile(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "prompt", required = false) String prompt,
            @RequestParam(value = "sessionId", required = false) String sessionId
    ) {
        return ApiResponse.ok(aiAssistantService.queryCozeAgentWithFile(prompt, sessionId, file));
    }

    @GetMapping("/reports/{userId}")
    public ApiResponse<List<AiDtos.AnalysisReportView>> listHistory(@PathVariable String userId) {
        return ApiResponse.ok(aiAssistantService.listHistory(userId));
    }
}
