package com.zhituan.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhituan.backend.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class CozeStreamClient {

    private static final String DEFAULT_STREAM_URL = "https://k7zcnck7q6.coze.site/stream_run";

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${coze.stream-url:" + DEFAULT_STREAM_URL + "}")
    private String streamUrl;

    @Value("${coze.token:}")
    private String token;

    @Value("${coze.project-id:}")
    private String projectId;

    @Value("${coze.session-id:}")
    private String defaultSessionId;

    @Value("${coze.connect-timeout-seconds:15}")
    private int connectTimeoutSeconds;

    @Value("${coze.read-timeout-seconds:120}")
    private int readTimeoutSeconds;

    public CozeStreamClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    public CozeResult streamRun(String prompt, String sessionId) {
        if (!StringUtils.hasText(prompt)) {
            throw new BusinessException("prompt 不能为空");
        }
        if (!StringUtils.hasText(token)) {
            throw new BusinessException("未配置 coze.token，请先在 application-local.yml 配置");
        }
        if (!StringUtils.hasText(projectId)) {
            throw new BusinessException("未配置 coze.project-id，请先在 application-local.yml 配置");
        }

        String resolvedSessionId = StringUtils.hasText(sessionId) ? sessionId : defaultSessionId;
        if (!StringUtils.hasText(resolvedSessionId)) {
            throw new BusinessException("sessionId 不能为空，且未配置默认 coze.session-id");
        }

        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(buildBody(prompt, resolvedSessionId));
        } catch (Exception e) {
            throw new BusinessException("构造 Coze 请求失败: " + e.getMessage());
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(streamUrl))
                .timeout(Duration.ofSeconds(Math.max(10, readTimeoutSeconds)))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .header("Accept", "text/event-stream")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<java.io.InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String errText = new String(response.body().readAllBytes(), StandardCharsets.UTF_8);
                throw new BusinessException("Coze 调用失败，status=" + response.statusCode() + ", body=" + errText);
            }
            return parseSse(response.body(), resolvedSessionId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Coze 调用异常: " + e.getMessage());
        }
    }

    private Map<String, Object> buildBody(String prompt, String sessionId) {
        Map<String, Object> text = Map.of("text", prompt);
        Map<String, Object> promptItem = Map.of("type", "text", "content", text);
        Map<String, Object> query = Map.of("prompt", java.util.List.of(promptItem));
        Map<String, Object> content = Map.of("query", query);

        Map<String, Object> body = new HashMap<>();
        body.put("content", content);
        body.put("type", "query");
        body.put("session_id", sessionId);
        body.put("project_id", projectId);
        return body;
    }

    private CozeResult parseSse(java.io.InputStream inputStream, String sessionId) throws IOException {
        StringBuilder answer = new StringBuilder();
        int eventCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder block = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    eventCount += processBlock(block.toString(), answer);
                    block.setLength(0);
                    continue;
                }
                block.append(line).append('\n');
            }
            if (!block.isEmpty()) {
                eventCount += processBlock(block.toString(), answer);
            }
        }

        return new CozeResult(answer.toString().trim(), eventCount, sessionId);
    }

    private int processBlock(String blockText, StringBuilder answer) {
        if (!StringUtils.hasText(blockText)) {
            return 0;
        }
        StringBuilder data = new StringBuilder();
        for (String line : blockText.split("\\n")) {
            if (line.startsWith("data:")) {
                data.append(line.substring(5).trim()).append('\n');
            }
        }

        String dataText = data.toString().trim();
        if (!StringUtils.hasText(dataText) || "[DONE]".equals(dataText)) {
            return 0;
        }

        try {
            JsonNode root = objectMapper.readTree(dataText);
            String extracted = extractText(root);
            if (StringUtils.hasText(extracted)) {
                if (!answer.isEmpty()) {
                    answer.append('\n');
                }
                answer.append(extracted.trim());
            }
            return 1;
        } catch (Exception ignored) {
            return 0;
        }
    }

    private String extractText(JsonNode node) {
        String[] candidatePaths = {
                "content.text",
                "message.content.text",
                "delta.content.text",
                "data.content.text",
                "answer",
                "output",
                "text"
        };
        for (String path : candidatePaths) {
            JsonNode value = findByPath(node, path);
            if (value != null && value.isTextual() && StringUtils.hasText(value.asText())) {
                return value.asText();
            }
        }

        StringBuilder merged = new StringBuilder();
        collectTextFields(node, merged);
        return merged.toString().trim();
    }

    private JsonNode findByPath(JsonNode node, String path) {
        JsonNode current = node;
        for (String seg : path.split("\\.")) {
            if (current == null || current.isMissingNode()) {
                return null;
            }
            current = current.get(seg);
        }
        return current;
    }

    private void collectTextFields(JsonNode node, StringBuilder merged) {
        if (node == null || node.isNull()) {
            return;
        }
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                if ("text".equalsIgnoreCase(entry.getKey())
                        && entry.getValue().isTextual()
                        && StringUtils.hasText(entry.getValue().asText())) {
                    if (!merged.isEmpty()) {
                        merged.append('\n');
                    }
                    merged.append(entry.getValue().asText().trim());
                } else {
                    collectTextFields(entry.getValue(), merged);
                }
            });
        } else if (node.isArray()) {
            node.forEach(item -> collectTextFields(item, merged));
        }
    }

    public record CozeResult(String answer, int eventCount, String sessionId) {
    }
}
