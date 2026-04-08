package com.zhituan.backend.service.impl;

import com.zhituan.backend.domain.model.ai.AnalysisReport;
import com.zhituan.backend.domain.model.ai.RiskItem;
import com.zhituan.backend.dto.AiDtos;
import com.zhituan.backend.common.exception.BusinessException;
import com.zhituan.backend.repository.ai.AnalysisReportRepository;
import com.zhituan.backend.repository.ai.RiskItemRepository;
import com.zhituan.backend.service.AiAssistantService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class AiAssistantServiceImpl implements AiAssistantService {

    private final AnalysisReportRepository analysisReportRepository;
    private final RiskItemRepository riskItemRepository;
    private final CozeStreamClient cozeStreamClient;
    private final DocumentTextExtractor documentTextExtractor;

    public AiAssistantServiceImpl(AnalysisReportRepository analysisReportRepository,
                                  RiskItemRepository riskItemRepository,
                                  CozeStreamClient cozeStreamClient,
                                  DocumentTextExtractor documentTextExtractor) {
        this.analysisReportRepository = analysisReportRepository;
        this.riskItemRepository = riskItemRepository;
        this.cozeStreamClient = cozeStreamClient;
        this.documentTextExtractor = documentTextExtractor;
    }

    @Override
    public AiDtos.AnalysisReportView analyzeContract(AiDtos.AnalyzeRequest request) {
        return analyzeByType("CONTRACT", request);
    }

    @Override
    public AiDtos.AnalysisReportView detectBlackSlang(AiDtos.AnalyzeRequest request) {
        return analyzeByType("BLACK_SLANG", request);
    }

    @Override
    public AiDtos.AnalysisReportView evaluateJobRisk(AiDtos.AnalyzeRequest request) {
        return analyzeByType("JOB_RISK", request);
    }

    @Override
    public AiDtos.CozeQueryResponse queryCozeAgent(AiDtos.CozeQueryRequest request) {
        CozeStreamClient.CozeResult result = cozeStreamClient.streamRun(request.prompt(), request.sessionId());
        return new AiDtos.CozeQueryResponse(result.answer(), result.eventCount(), result.sessionId());
    }

    @Override
    public SseEmitter queryCozeAgentStream(AiDtos.CozeQueryRequest request) {
        SseEmitter emitter = new SseEmitter(0L);

        CompletableFuture.runAsync(() -> {
            try {
                emitter.send(SseEmitter.event().name("ready").data("connected"));

                CozeStreamClient.CozeResult result = cozeStreamClient.streamRun(request.prompt(), request.sessionId(), chunk -> {
                    if (!StringUtils.hasText(chunk)) {
                        return;
                    }
                    try {
                        emitter.send(SseEmitter.event().name("chunk").data(chunk));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                emitter.send(SseEmitter.event()
                        .name("done")
                        .data(Map.of(
                                "sessionId", result.sessionId(),
                                "eventCount", result.eventCount(),
                                "answer", result.answer()
                        )));
                emitter.complete();
            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event().name("error").data(e.getMessage()));
                } catch (Exception ignored) {
                }
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    @Override
    public SseEmitter queryCozeAgentWithFileStream(String prompt, String sessionId, MultipartFile file) {
        if (!StringUtils.hasText(prompt) && (file == null || file.isEmpty())) {
            throw new BusinessException("prompt 和 file 不能同时为空");
        }

        SseEmitter emitter = new SseEmitter(0L);
        CompletableFuture.runAsync(() -> {
            try {
                emitter.send(SseEmitter.event().name("ready").data("connected"));

                String mergedPrompt = prompt;
                String fileName = null;
                String fileType = null;
                int extractedLen = 0;

                if (file != null && !file.isEmpty()) {
                    emitter.send(SseEmitter.event().name("phase").data("extracting"));
                    DocumentTextExtractor.ExtractResult extracted = documentTextExtractor.extract(file);
                    mergedPrompt = buildFilePrompt(prompt, extracted.text());
                    fileName = extracted.fileName();
                    fileType = extracted.fileType();
                    extractedLen = extracted.text().length();
                    emitter.send(SseEmitter.event().name("phase").data("calling-coze"));
                }

                CozeStreamClient.CozeResult result = cozeStreamClient.streamRun(mergedPrompt, sessionId, chunk -> {
                    if (!StringUtils.hasText(chunk)) {
                        return;
                    }
                    try {
                        emitter.send(SseEmitter.event().name("chunk").data(chunk));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                emitter.send(SseEmitter.event()
                        .name("done")
                        .data(Map.of(
                                "sessionId", result.sessionId(),
                                "eventCount", result.eventCount(),
                                "answer", result.answer(),
                                "fileName", fileName == null ? "" : fileName,
                                "fileType", fileType == null ? "" : fileType,
                                "extractedTextLength", extractedLen
                        )));
                emitter.complete();
            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event().name("error").data(e.getMessage()));
                } catch (Exception ignored) {
                }
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    @Override
    public AiDtos.CozeFileQueryResponse queryCozeAgentWithFile(String prompt, String sessionId, MultipartFile file) {
        if (!StringUtils.hasText(prompt) && (file == null || file.isEmpty())) {
            throw new BusinessException("prompt 和 file 不能同时为空");
        }

        if (file == null || file.isEmpty()) {
            CozeStreamClient.CozeResult textOnlyResult = cozeStreamClient.streamRun(prompt, sessionId);
            return new AiDtos.CozeFileQueryResponse(
                    textOnlyResult.answer(),
                    textOnlyResult.eventCount(),
                    textOnlyResult.sessionId(),
                    null,
                    null,
                    0
            );
        }

        DocumentTextExtractor.ExtractResult extracted = documentTextExtractor.extract(file);
        String mergedPrompt = buildFilePrompt(prompt, extracted.text());
        CozeStreamClient.CozeResult result = cozeStreamClient.streamRun(mergedPrompt, sessionId);
        return new AiDtos.CozeFileQueryResponse(
                result.answer(),
                result.eventCount(),
                result.sessionId(),
                extracted.fileName(),
                extracted.fileType(),
                extracted.text().length()
        );
    }

    @Override
    public List<AiDtos.AnalysisReportView> listHistory(String userId) {
        return analysisReportRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(report -> new AiDtos.AnalysisReportView(
                        report.getReportId(),
                        report.getBusinessType(),
                        report.getScore(),
                        report.getOverallRiskLevel(),
                        riskItemRepository.findByReportId(report.getReportId()).stream()
                                .map(item -> new AiDtos.RiskItemView(
                                        item.getMatchedText(),
                                        item.getRiskLevel(),
                                        item.getReason(),
                                        item.getSuggestion()
                                ))
                                .toList()
                ))
                .toList();
    }

    private AiDtos.AnalysisReportView analyzeByType(String businessType, AiDtos.AnalyzeRequest request) {
        // [纯模拟实现] 暂时不调用真实的 AI API，直接根据输入的文本长度随机生成一个分数
        int score = Math.min(95, Math.max(5, request.text().length() % 100)); // 随便算个伪随机分数
        String riskLevel = score >= 80 ? "HIGH" : score >= 50 ? "MEDIUM" : "LOW";

        // 1. 创建假的 AI 分析报告并入库
        AnalysisReport report = AnalysisReport.builder()
                .userId(request.userId())
                .businessType(businessType)
                // 截取前60个字作为摘要
                .inputSummary(request.text().substring(0, Math.min(60, request.text().length())))
                .score(score)
                .overallRiskLevel(riskLevel)
                .createdAt(LocalDateTime.now())
                .build();
        report.calculateOverallRiskLevel(); // 使用我们刚加的充血模型方法
        AnalysisReport saved = analysisReportRepository.save(report);

        // 2. 也是假装发现了一个风险点
        RiskItem item = RiskItem.builder()
                .reportId(saved.getReportId())
                .matchedText(request.text().substring(0, Math.min(10, request.text().length()))) // 随便截几个字假装命中
                .startIndex(0)
                .endIndex(Math.min(10, request.text().length()))
                .riskLevel(riskLevel)
                .reason(businessType.equals("CONTRACT") ? "该条款可能存在试用期过长或克扣工资的霸王条款风险（模拟）" : "检测到疑似招聘黑话骗局（模拟）")
                .legalBasis("《中华人民共和国劳动合同法》第十九条：劳动合同期限三个月以上不满一年的，试用期不得超过一个月。")
                .suggestion(businessType.equals("CONTRACT") ? "建议明确试用期具体时长及薪资约定比例。" : "警惕用人单位以培训名义收取费用。")
                .build();
        RiskItem savedItem = riskItemRepository.save(item);

        return new AiDtos.AnalysisReportView(
                saved.getReportId(),
                saved.getBusinessType(),
                saved.getScore(),
                saved.getOverallRiskLevel(),
                List.of(new AiDtos.RiskItemView(
                        savedItem.getMatchedText(),
                        savedItem.getRiskLevel(),
                        savedItem.getReason(),
                        savedItem.getSuggestion()
                ))
        );
    }

    private String buildFilePrompt(String prompt, String extractedText) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(prompt)) {
            sb.append("用户补充要求：\n").append(prompt.trim()).append("\n\n");
        } else {
            sb.append("请基于下面上传的劳动合同或三方协议文本，做风险审查，输出主要风险点与修改建议。\n\n");
        }
        sb.append("文档正文：\n").append(extractedText);
        return sb.toString();
    }
}
